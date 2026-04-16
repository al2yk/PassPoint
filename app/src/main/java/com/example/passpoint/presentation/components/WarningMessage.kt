package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.theme.Red100
import com.example.passpoint.presentation.theme.Red800

@Composable
fun WarningMessage(text: String){
    SuggestionChip(
        onClick = {},
        label = {
            Text(
                text = text,
                color = Red800,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        modifier = Modifier.fillMaxWidth().height(32.dp),
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = Red100
        ),
        shape = MaterialTheme.shapes.small,
        enabled = false
    )
}