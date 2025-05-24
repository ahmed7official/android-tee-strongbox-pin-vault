package com.ahmed7official.securetee.vault.domain.crypto

import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import javax.inject.Inject

class EncryptTextUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(
        plainText: String,
        keyAlias: String,
        authenticator: BiometricAuthenticator? = null,
    ): String {
        return cryptoRepository.encrypt(
            plainText = plainText,
            keyAlias = keyAlias,
            authenticator = authenticator,
        )
    }
} 