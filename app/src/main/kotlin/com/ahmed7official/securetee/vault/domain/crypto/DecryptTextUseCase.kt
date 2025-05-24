package com.ahmed7official.securetee.vault.domain.crypto

import javax.inject.Inject

class DecryptTextUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(encryptedText: String, keyAlias: String): String {
        return cryptoRepository.decrypt(encryptedText, keyAlias)
    }
} 