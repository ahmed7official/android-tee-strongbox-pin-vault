package com.ahmed7official.securetee.vault.domain.pin

import kotlinx.serialization.Serializable

@Serializable
data class PinData(
    val id: String = "",
    val label: String,
    val value: String,
    val createdAt: Long = System.currentTimeMillis()
) 