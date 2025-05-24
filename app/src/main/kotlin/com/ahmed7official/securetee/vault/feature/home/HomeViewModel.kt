package com.ahmed7official.securetee.vault.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed7official.securetee.vault.domain.pin.PinData
import com.ahmed7official.securetee.vault.domain.pin.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val pinRepository: PinRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchPins()
    }

    private fun fetchPins() {
        viewModelScope.launch {
            pinRepository.pins
                .onStart { _uiState.value = HomeUiState.Loading }
                .catch { exception -> _uiState.value = HomeUiState.Error(exception.message ?: "Unknown Error") }
                .collect { result -> _uiState.value = HomeUiState.Success(result) }
        }
    }

}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val pins: List<PinData>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
