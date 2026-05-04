package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passpoint.data.dto.Event
import com.example.passpoint.domain.utils.formatDateRu
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.White

@Composable
fun EventCard(
    event: Event,
    isRegistered: Boolean,
    isRegistrationLoading: Boolean,
    onRegisterClick: () -> Unit,
    onUnregisterClick: () -> Unit,
    showButtons: Boolean = true
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.name, style = MaterialTheme.typography.headlineSmall)
            SpacerHeight(14)
            Text("Дата проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
            Text(text = formatDateRu(event.date), style = MaterialTheme.typography.bodyLarge)
            SpacerHeight(7)
            Text("Место проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
            Text(event.place, style = MaterialTheme.typography.bodyLarge)
            SpacerHeight(16)

            if (showButtons) {
                if (isRegistered) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onUnregisterClick,
                            modifier = Modifier.weight(1f).height(ButtonHeight),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isRegistrationLoading
                        ) {
                            Text("Отменить", style = MaterialTheme.typography.displaySmall)
                        }
                        Button(
                            onClick = { /* показать QR */ },
                            modifier = Modifier.weight(1f).height(ButtonHeight),
                            enabled = !isRegistrationLoading,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
                        ) {
                            Text("Показать QR", color = White, style = MaterialTheme.typography.displaySmall)
                        }
                    }
                } else {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth().height(ButtonHeight),
                        enabled = !isRegistrationLoading,
                        onClick = onRegisterClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Участвовать", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}