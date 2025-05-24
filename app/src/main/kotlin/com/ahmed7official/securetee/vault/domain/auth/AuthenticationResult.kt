package com.ahmed7official.securetee.vault.domain.auth

sealed interface AuthenticationResult {
    data object Success : AuthenticationResult
    data class Fail(
        val errorMessage: String,
    ) : AuthenticationResult
}