package com.ahmed7official.securetee.vault.presentation.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ahmed7official.securetee.vault.domain.auth.AuthenticationResult
import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume


open class BiometricAuthenticatorImpl @Inject constructor(
    private val activity: FragmentActivity,
) : BiometricAuthenticator {

    override suspend fun authenticateWithCipher(cipher: javax.crypto.Cipher): AuthenticationResult =
        suspendCancellableCoroutine { continuation ->
            val biometricManager = BiometricManager.from(activity)
            val canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )

            if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
                continuation.resume(AuthenticationResult.Fail("Biometric authentication is not available."))
                return@suspendCancellableCoroutine
            }

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Log.d("BiometricAuthenticator", "Authentication succeeded")
                    continuation.resume(AuthenticationResult.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.e("BiometricAuthenticator", "Authentication error: $errString")
                    continuation.resume(AuthenticationResult.Fail(errString.toString()))
                }

                override fun onAuthenticationFailed() {
                    Log.e("BiometricAuthenticator", "Authentication failed")
                    // Authentication continues, don't resume yet
                }
            }

            val executor = ContextCompat.getMainExecutor(activity)
            activity.runOnUiThread {
                val biometricPrompt = BiometricPrompt(activity, executor, callback)
                try {
                    biometricPrompt.authenticate(
                        createPromptInfo(),
                        BiometricPrompt.CryptoObject(cipher)
                    )
                } catch (e: Exception) {
                    Log.e("BiometricAuthenticator", "Authentication failed", e)
                    continuation.resume(AuthenticationResult.Fail(e.message ?: "Unknown error"))
                }

                continuation.invokeOnCancellation {
                    biometricPrompt.cancelAuthentication()
                }
            }
        }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to access encrypted data").setNegativeButtonText("Cancel")
            .build()
    }
} 