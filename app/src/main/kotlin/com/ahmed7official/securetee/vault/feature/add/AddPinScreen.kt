package com.ahmed7official.securetee.vault.feature.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmed7official.securetee.vault.presentation.auth.BiometricAuthenticatorImpl
import com.ahmed7official.securetee.vault.ui.theme.PINVaultTheme

@Composable
fun AddPinScreen(
    viewModel: AddPinViewModel = hiltViewModel(),
    onPinAdded: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val authenticator = BiometricAuthenticatorImpl(context as FragmentActivity)

    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    // Handle success state
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPinAdded()
            viewModel.resetState()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Add New PIN",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = state.label,
            onValueChange = viewModel::updateLabel,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Label") },
            placeholder = { Text("e.g., Bank Card, Door Code") },
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            value = state.value,
            onValueChange = viewModel::updateValue,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("PIN") },
            placeholder = { Text("e.g., 1234") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = { 
                focusManager.clearFocus()
                viewModel.addPin(authenticator)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.label.isNotBlank() && state.value.isNotBlank()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            }
            Text(text = "Add PIN")
        }
        
        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.padding(16.dp))
            Snackbar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPinScreenPreview() {
    PINVaultTheme {
        AddPinScreen()
    }
}
