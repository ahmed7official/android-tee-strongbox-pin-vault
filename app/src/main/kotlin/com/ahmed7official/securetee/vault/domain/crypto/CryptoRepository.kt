package com.ahmed7official.securetee.vault.domain.crypto

import androidx.fragment.app.FragmentActivity
import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator

interface CryptoRepository {
    /**
     * Encrypt plaintext data with a given key
     * Authentication will be requested if needed via the authenticator
     */
    suspend fun encrypt(plainText: String, keyAlias: String, authenticator: BiometricAuthenticator? = null): String
    
    /**
     * Decrypt encrypted data with a given key
     * Authentication will be requested if needed via the authenticator
     * @param activity Required if authenticator is provided
     */
    suspend fun decrypt(encryptedText: String, keyAlias: String, authenticator: BiometricAuthenticator? = null, activity: FragmentActivity? = null): String
    
    /**
     * Encrypt plaintext data to a file with a given key
     * Authentication will be requested if needed via the authenticator
     */
    suspend fun encryptToFile(plainText: String, fileName: String, keyAlias: String, authenticator: BiometricAuthenticator? = null)
    
    /**
     * Decrypt data from a file with a given key
     * Authentication will be requested if needed via the authenticator
     * @param activity Required if authenticator is provided
     */
    suspend fun decryptFromFile(fileName: String, keyAlias: String, authenticator: BiometricAuthenticator? = null, activity: FragmentActivity? = null): String
    
    /**
     * Delete an encrypted file
     */
    suspend fun deleteEncryptedFile(fileName: String): Boolean
    
    /**
     * List all encrypted files
     */
    suspend fun listEncryptedFiles(): List<String>
} 