package com.ahmed7official.securetee.vault.domain.auth

interface BiometricAuthenticator {

    suspend fun authenticateWithCipher(cipher: javax.crypto.Cipher): AuthenticationResult
} 