package com.ahmed7official.securetee.vault.domain.crypto

import javax.inject.Inject

class EncryptToFileUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(plainText: String, fileName: String, keyAlias: String) {
        cryptoRepository.encryptToFile(plainText, fileName, keyAlias)
    }
} 