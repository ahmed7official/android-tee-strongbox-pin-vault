package com.ahmed7official.securetee.vault.feature.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import com.ahmed7official.securetee.vault.domain.pin.AddPinUseCase
import com.ahmed7official.securetee.vault.domain.pin.PinData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPinViewModel @Inject constructor(
    private val addPinUseCase: AddPinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPinUiState())
    val uiState: StateFlow<AddPinUiState> = _uiState.asStateFlow()

    fun updateLabel(label: String) {
        _uiState.update { it.copy(label = label) }
    }

    fun updateValue(value: String) {
        _uiState.update { it.copy(value = value) }
    }


    fun addPin(authenticator: BiometricAuthenticator) {
        val currentState = _uiState.value
        
        if (currentState.label.isBlank() || currentState.value.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Label and value cannot be empty") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val newPin = addPinUseCase(currentState.label, currentState.value, authenticator)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        addedPin = newPin
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to add pin"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddPinUiState() }
    }
}

data class AddPinUiState(
    val label: String = "",
    val value: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val addedPin: PinData? = null
) 