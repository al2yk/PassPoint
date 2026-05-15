package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.ui.theme.White60

@Composable
fun AuthTitle(title: String, isEnabled:Boolean = true) {
    Text(
        title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        color = if(isEnabled) White else White60,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp)
    )
}