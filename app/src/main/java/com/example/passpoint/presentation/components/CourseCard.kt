package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.domain.utils.formatDateRu
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.White

@Composable
fun CourseCard(
    course: CourseWithEnrollment,
    isRegistered: Boolean,
    isRegistrationLoading: Boolean,
    onRegisterClick: () -> Unit,
    onUnregisterClick: () -> Unit,
    showButtons: Boolean = true,
    showCapacity: Boolean = true
) {
    Text(course.name, style = MaterialTheme.typography.headlineSmall)
    SpacerHeight(14)
    Text(course.description, style = MaterialTheme.typography.displaySmall, color = Gray600)
    SpacerHeight(14)
    Text("Дата проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
    Text(text = formatDateRu(course.date), style = MaterialTheme.typography.bodyLarge)
    SpacerHeight(7)
    Text("Место проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
    Text(course.place, style = MaterialTheme.typography.bodyLarge)

    if (showCapacity) {
        SpacerHeight(7)
        Text("Количество мест", style = MaterialTheme.typography.displaySmall, color = Gray600)
        Text(
            "Осталось: ${course.capacity - course.enrolled_count}",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    SpacerHeight(16)

    if (showButtons) {
        if (isRegistered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onUnregisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(ButtonHeight),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isRegistrationLoading
                ) {
                    Text("Отменить", style = MaterialTheme.typography.displaySmall)
                }
                Button(
                    onClick = { /* показать QR */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(ButtonHeight),
                    enabled = !isRegistrationLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Показать QR",
                        color = White,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        } else {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                enabled = !isRegistrationLoading,
                onClick = onRegisterClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Участвовать",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
