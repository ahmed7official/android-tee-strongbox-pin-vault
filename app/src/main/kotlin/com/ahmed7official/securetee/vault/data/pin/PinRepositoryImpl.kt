package com.ahmed7official.securetee.vault.data.pin

import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import com.ahmed7official.securetee.vault.domain.crypto.CryptoRepository
import com.ahmed7official.securetee.vault.domain.pin.PinData
import com.ahmed7official.securetee.vault.domain.pin.PinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinRepositoryImpl @Inject constructor(
    private val cryptoRepository: CryptoRepository
) : PinRepository {


    private val keyAlias = "pin_vault_key_v2"
    private val json = Json { ignoreUnknownKeys = true }

    private val _pins = MutableStateFlow<List<PinData>>(emptyList())
    override val pins = _pins


    override fun fetchPins() {
        CoroutineScope(Dispatchers.IO).launch {
            val fileNames = cryptoRepository.listEncryptedFiles()
            val results = fileNames.filter { it.startsWith("pin_") && it.endsWith(".json") }
                .mapNotNull { fileName ->
                    try {
                        val pinJson = cryptoRepository.decryptFromFile(fileName, keyAlias)
                        json.decodeFromString<PinData>(pinJson)
                    } catch (e: Exception) {
                        null
                    }
                }
            _pins.update { results }
        }
    }


    override suspend fun addPin(
        label: String,
        value: String,
        authenticator: BiometricAuthenticator?,
    ): PinData {
        val pin = PinData(
            id = UUID.randomUUID().toString(),
            label = label,
            value = value
        )

        val pinJson = json.encodeToString(pin)
        cryptoRepository.encryptToFile(pinJson, "pin_${pin.id}.json", keyAlias, authenticator)
        _pins.update { it + pin }
        return pin
    }

    override suspend fun deletePin(id: String): Boolean {
        return cryptoRepository.deleteEncryptedFile("pin_$id.json")
    }

    override fun getAllPins(): Flow<List<PinData>> = flow {
        val fileNames = cryptoRepository.listEncryptedFiles()
        val pins = fileNames
            .filter { it.startsWith("pin_") && it.endsWith(".json") }
            .mapNotNull { fileName ->
                try {
                    val pinJson = cryptoRepository.decryptFromFile(fileName, keyAlias)
                    json.decodeFromString<PinData>(pinJson)
                } catch (e: Exception) {
                    null
                }
            }
        emit(pins)
    }.flowOn(Dispatchers.IO)

    override suspend fun getPin(id: String): PinData {
        val pinJson = cryptoRepository.decryptFromFile("pin_$id.json", keyAlias)
        return json.decodeFromString(pinJson)
    }
} 