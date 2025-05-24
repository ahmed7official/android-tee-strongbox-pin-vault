package com.ahmed7official.securetee.vault.domain.pin

import javax.inject.Inject

class GetPinUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    suspend operator fun invoke(pinId: String): PinData {
        return pinRepository.getPin(pinId)
    }
} 