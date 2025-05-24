package com.ahmed7official.securetee.vault.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed7official.securetee.vault.domain.pin.GetPinUseCase
import com.ahmed7official.securetee.vault.domain.pin.PinData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinDetailsViewModel @Inject constructor(
    private val getPinUseCase: GetPinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinDetailsUiState())
    val uiState: StateFlow<PinDetailsUiState> = _uiState.asStateFlow()

    fun loadPin(pinId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val pin = getPinUseCase(pinId)
                _uiState.update { 
                    it.copy(
                        pin = pin,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load PIN details"
                    )
                }
            }
        }
    }
}

data class PinDetailsUiState(
    val pin: PinData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 