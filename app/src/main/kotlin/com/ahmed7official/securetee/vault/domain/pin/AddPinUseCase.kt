package com.ahmed7official.securetee.vault.domain.pin

import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import javax.inject.Inject

class AddPinUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    suspend operator fun invoke(
        label: String,
        value: String,
        authenticator: BiometricAuthenticator? = null,
    ): PinData {
        return pinRepository.addPin(label, value, authenticator)
    }
} 