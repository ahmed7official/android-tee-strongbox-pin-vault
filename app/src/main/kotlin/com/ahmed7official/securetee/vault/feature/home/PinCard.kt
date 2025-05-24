package com.ahmed7official.securetee.vault.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ahmed7official.securetee.vault.ui.theme.PINVaultTheme

@Composable
fun PinCard(
    label: String,
    modifier: Modifier = Modifier,
    onPinClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPinClick() }
    ) {
        Text(text = label, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PinCardPreview() {
    PINVaultTheme {
        PinCard(label = "Master Card")
    }
}
