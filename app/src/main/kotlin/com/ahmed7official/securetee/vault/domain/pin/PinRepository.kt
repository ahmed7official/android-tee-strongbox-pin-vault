package com.ahmed7official.securetee.vault.domain.pin

import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PinRepository {

    val pins: Flow<List<PinData>>

    fun fetchPins()

    suspend fun addPin(
        label: String,
        value: String,
        authenticator: BiometricAuthenticator? = null,
    ): PinData

    suspend fun deletePin(id: String): Boolean
    fun getAllPins(): Flow<List<PinData>>
    suspend fun getPin(id: String): PinData
} 