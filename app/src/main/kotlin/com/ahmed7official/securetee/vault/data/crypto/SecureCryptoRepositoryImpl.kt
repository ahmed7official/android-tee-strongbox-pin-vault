package com.ahmed7official.securetee.vault.data.crypto

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.ahmed7official.securetee.vault.domain.auth.AuthenticationResult
import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import com.ahmed7official.securetee.vault.domain.crypto.CryptoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureCryptoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CryptoRepository {

    companion object {
        private const val TAG = "SecureCryptoRepo"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
        private const val TRANSFORMATION =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
        private const val FILES_DIR_NAME = "encrypted_files"
        private const val AUTH_VALIDITY_DURATION_SECONDS = 60
    }

    // Cache directory for encrypted files
    private var _encryptedFilesDir: File? = null
    private fun getEncryptedFilesDir(): File {
        if (_encryptedFilesDir == null) {
            val dir = File(context.filesDir, FILES_DIR_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            _encryptedFilesDir = dir
        }
        return _encryptedFilesDir!!
    }

    override suspend fun encrypt(
        plainText: String,
        keyAlias: String,
        authenticator: BiometricAuthenticator?,
    ): String = withContext(Dispatchers.IO) {
        try {
            val key = getOrCreateSecretKey(keyAlias)
            val cipher = Cipher.getInstance(TRANSFORMATION)

            try {
                // Let Android KeyStore generate the IV
                cipher.init(Cipher.ENCRYPT_MODE, key)
            } catch (e: UserNotAuthenticatedException) {
                e.printStackTrace()
                if (authenticator == null) {
                    throw SecurityException("User authentication required but no authenticator provided")
                }

                val result = authenticator.authenticateWithCipher(cipher)

                when (result) {
                    is AuthenticationResult.Fail -> throw SecurityException("Authentication failed")
                    AuthenticationResult.Success -> {
                        // Reinitialize cipher after authentication
                        cipher.init(Cipher.ENCRYPT_MODE, key)
                    }
                }

            }

            // Encrypt data with the authenticated cipher
            encryptWithCipher(plainText, cipher)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption error", e)
            throw e
        }
    }

    override suspend fun decrypt(
        encryptedText: String,
        keyAlias: String,
        authenticator: BiometricAuthenticator?,
        activity: FragmentActivity?
    ): String = withContext(Dispatchers.IO) {
        try {
            val key = getOrCreateSecretKey(keyAlias)
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

            // Extract IV from the combined data
            val iv = ByteArray(GCM_IV_LENGTH)
            val encryptedBytes = ByteArray(combined.size - GCM_IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)
            System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)

            try {
                cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            } catch (e: UserNotAuthenticatedException) {
                e.printStackTrace()
                if (authenticator == null) {
                    Log.e(TAG, "User authentication required but no authenticator provided")
                    throw SecurityException("User authentication required but no authenticator provided")
                }

                if (activity == null) {
                    Log.e(TAG, "User authentication required but no authenticator provided")
                    throw SecurityException("Activity required for biometric authentication")
                }


                val result = authenticator.authenticateWithCipher(cipher)

                when (result) {
                    is AuthenticationResult.Fail -> throw SecurityException("Authentication failed")
                    AuthenticationResult.Success -> {
                        // Reinitialize cipher after authentication
                        Log.e(TAG, "User authentication required but no authenticator provided")
                        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
                    }
                }

            }

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption error", e)
            throw e
        }
    }

    override suspend fun encryptToFile(
        plainText: String,
        fileName: String,
        keyAlias: String,
        authenticator: BiometricAuthenticator?,
    ) = withContext(Dispatchers.IO) {
        try {
            val key = getOrCreateSecretKey(keyAlias)
            val fileToWrite = File(getEncryptedFilesDir(), fileName)

            val cipher = Cipher.getInstance(TRANSFORMATION)

            try {
                // Let Android KeyStore generate the IV
                cipher.init(Cipher.ENCRYPT_MODE, key)
            } catch (e: UserNotAuthenticatedException) {
                e.printStackTrace()
                if (authenticator == null) {
                    Log.e(TAG, "User authentication required but no authenticator provided")
                    throw SecurityException("User authentication required but no authenticator provided")
                }

                val result = authenticator.authenticateWithCipher(cipher)

                when (result) {
                    is AuthenticationResult.Fail -> {
                        Log.e(TAG, "result - User authentication required but no authenticator provided")
                        throw SecurityException("Authentication failed")
                    }

                    AuthenticationResult.Success -> {
                        Log.e(TAG, "User authentication required but no authenticator provided")
                        // Reinitialize cipher after authentication
                        cipher.init(Cipher.ENCRYPT_MODE, key)
                    }
                }
            }

            // Get the IV that KeyStore generated
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            // Write IV + encrypted data to file
            FileOutputStream(fileToWrite).use { outputStream ->
                // Write IV first
                outputStream.write(iv)
                // Then write encrypted data
                outputStream.write(encryptedBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "File encryption error", e)
            throw e
        }
    }

    override suspend fun decryptFromFile(
        fileName: String,
        keyAlias: String,
        authenticator: BiometricAuthenticator?,
        activity: FragmentActivity?
    ): String = withContext(Dispatchers.IO) {
        try {
            val key = getOrCreateSecretKey(keyAlias)
            val fileToRead = File(getEncryptedFilesDir(), fileName)

            if (!fileToRead.exists()) {
                throw IOException("File does not exist: $fileName")
            }

            // Read the file content first
            val iv = ByteArray(GCM_IV_LENGTH)
            val encryptedBytes: ByteArray

            FileInputStream(fileToRead).use { inputStream ->
                // Read IV from the beginning of the file
                if (inputStream.read(iv) != GCM_IV_LENGTH) {
                    throw IOException("Failed to read IV from file")
                }

                // Read the rest of the file as encrypted data
                encryptedBytes = ByteArrayOutputStream().use { byteStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        byteStream.write(buffer, 0, bytesRead)
                    }
                    byteStream.toByteArray()
                }
            }

            // Initialize the cipher
            val cipher = Cipher.getInstance(TRANSFORMATION)

            try {
                cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            } catch (e: UserNotAuthenticatedException) {
                e.printStackTrace()
                if (authenticator == null) {
                    throw SecurityException("User authentication required but no authenticator provided")
                }

                if (activity == null) {
                    throw SecurityException("Activity required for biometric authentication")
                }


                val result = authenticator.authenticateWithCipher(cipher)

                when (result) {
                    is AuthenticationResult.Fail -> throw SecurityException("Authentication failed")
                    AuthenticationResult.Success -> {
                        // Reinitialize cipher after authentication
                        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
                    }
                }
            }

            // Decrypt the data
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "File decryption error", e)
            throw e
        }
    }

    override suspend fun deleteEncryptedFile(fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val fileToDelete = File(getEncryptedFilesDir(), fileName)
            if (fileToDelete.exists()) {
                fileToDelete.delete()
            } else {
                false
            }
        }

    override suspend fun listEncryptedFiles(): List<String> = withContext(Dispatchers.IO) {
        val files = getEncryptedFilesDir().listFiles()
        files?.map { it.name } ?: emptyList()
    }

    /**
     * Encrypt plain text using an initialized cipher
     */
    private fun encryptWithCipher(plainText: String, cipher: Cipher): String {
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        // Combine IV and encrypted data
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun getOrCreateSecretKey(keyAlias: String): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        // Check if key already exists
        if (keyStore.containsAlias(keyAlias)) {
            val entry = keyStore.getEntry(keyAlias, null)
            return (entry as KeyStore.SecretKeyEntry).secretKey
        }

        // Generate a new key with StrongBox and TEE if available
        val useStrongBox =
            context.packageManager.hasSystemFeature("android.hardware.strongbox_keystore")

        val builder = KeyGenParameterSpec.Builder(
            keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )

        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        builder.setKeySize(256)
        builder.setRandomizedEncryptionRequired(true)

        if (useStrongBox) {
            builder.setIsStrongBoxBacked(true)
        }

        // Enable user authentication for key use
        builder.setUserAuthenticationRequired(false)

        // Use different approaches based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(
                AUTH_VALIDITY_DURATION_SECONDS,
                KeyProperties.AUTH_DEVICE_CREDENTIAL
            )
        } else {
            @Suppress("DEPRECATION") builder.setUserAuthenticationValidityDurationSeconds(
                AUTH_VALIDITY_DURATION_SECONDS
            )
        }

        builder.setUnlockedDeviceRequired(true)

        val keyGenParameterSpec = builder.build()

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER
        )
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
}
