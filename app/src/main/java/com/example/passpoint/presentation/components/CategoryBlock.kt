package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.ui.theme.ButtonHeight

@Composable
fun CategoryBlock(isSelected: Boolean, title: String, onClick: () -> Unit) {

    if (!isSelected) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .height(ButtonHeight),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                title, style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier.height(ButtonHeight),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inversePrimary
            )
        ) {
            Text(
                title, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}