package com.ahmed7official.securetee.vault.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PinDetailsScreen(
    pinId: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    viewModel: PinDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(pinId) {
        viewModel.loadPin(pinId)
    }
    
    val state by viewModel.uiState.collectAsState()
    
    state.pin?.let { pin ->
        PinDetailsDialog(
            pin = pin,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
} 