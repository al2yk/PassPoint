package com.example.passpoint.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.BrandTonal200

@Composable
fun AttendancePieChart(
    attended: Int,
    missed: Int,
    modifier: Modifier = Modifier
) {
    val total = attended + missed
    val attendedFraction = if (total > 0) attended.toFloat() / total else 0f
    val missedFraction = if (total > 0) missed.toFloat() / total else 0f
    val attendedAngle = attendedFraction * 360f
    val missedAngle = missedFraction * 360f

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (total > 0) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val strokeWidth = 35f
                val radius = size.minDimension / 2 - strokeWidth / 2
                val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
                val arcSize = Size(radius * 2, radius * 2)

                // Фон (лёгкий серый)
                drawArc(
                    color = Color(0xFFE0E0E0),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Сектор "Присутствовало"
                drawArc(
                    color = BrandColor,
                    startAngle = -90f,
                    sweepAngle = attendedAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Сектор "Отсутствовало"
                drawArc(
                    color = BrandTonal200,
                    startAngle = -90f + attendedAngle,
                    sweepAngle = missedAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            SpacerHeight(12)
        } else {
            Text("Нет данных", style = MaterialTheme.typography.bodyLarge)
        }
    }
}