package com.ahmed7official.securetee.vault.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmed7official.securetee.vault.domain.pin.PinData
import com.ahmed7official.securetee.vault.ui.theme.PINVaultTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddPin: () -> Unit = {},
    onPinSelected: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = HomeUiState.Loading
    )

    HomeScreenContent(
        state = uiState,
        onNavigateToAddPin = onNavigateToAddPin,
        onPinSelected = onPinSelected,
        modifier = modifier
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onNavigateToAddPin: () -> Unit,
    onPinSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is HomeUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }

                    is HomeUiState.Success -> {
                        val pins = state.pins
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (pins.isEmpty()) {
                                item {
                                    Text(
                                        text = "No PINs saved yet. Add your first PIN!",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(pins) { pin ->
                                    PinCard(
                                        label = pin.label,
                                        onPinClick = { onPinSelected(pin.id) }
                                    )
                                }
                            }

                            item {
                                Button(
                                    onClick = onNavigateToAddPin,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Rounded.Add),
                                        contentDescription = "Add New Pin",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(text = "Add New Pin")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val samplePins = listOf(
        PinData(id = "1", label = "VISA Card", value = "1234"),
        PinData(id = "2", label = "Master Card", value = "5678"),
        PinData(id = "3", label = "Door Lock", value = "9876")
    )

    val homeState = HomeUiState.Success(samplePins)

    PINVaultTheme {
        HomeScreenContent(
            state = homeState,
            onNavigateToAddPin = {},
            onPinSelected = {}
        )
    }
}
