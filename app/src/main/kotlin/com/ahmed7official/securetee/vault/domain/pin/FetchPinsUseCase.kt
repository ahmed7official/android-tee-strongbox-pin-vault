package com.ahmed7official.securetee.vault.domain.pin

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchPinsUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    operator fun invoke() = pinRepository.fetchPins()
} 