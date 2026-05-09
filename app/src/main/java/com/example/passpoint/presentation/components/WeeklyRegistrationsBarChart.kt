package com.example.passpoint.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.Gray800
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyRegistrationsBarChart(
    weeklyCounts: List<Int>,
    modifier: Modifier = Modifier
) {
    if (weeklyCounts.isEmpty() || weeklyCounts.all { it == 0 }) {
        Text("Нет данных по неделям", modifier = modifier, style = MaterialTheme.typography.bodyLarge)
        return
    }

    val maxCount = weeklyCounts.maxOrNull() ?: 1
    val formatter = DateTimeFormatter.ofPattern("dd.MM")
    val today = LocalDate.now()
    val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
    val labels = (0..4).map { i ->
        val weekStart = startOfWeek.minusWeeks((4 - i).toLong())
        val weekEnd = weekStart.plusDays(6)
        "${weekStart.format(formatter)}-${weekEnd.format(formatter)}"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Регистрации по неделям",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val barCount = weeklyCounts.size
            val totalBarWidth = size.width / barCount
            val barWidth = totalBarWidth * 0.6f
            val spacing = (totalBarWidth - barWidth) / 2

            weeklyCounts.forEachIndexed { index, count ->
                val barHeight = (count.toFloat() / maxCount) * (size.height - 40f)
                val x = index * totalBarWidth + spacing
                val y = size.height - 40f - barHeight

                drawRect(
                    color = BrandColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    count.toString(),
                    x + barWidth / 2,
                    y - 8f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 12 * density
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = Gray800
                )
            }
        }
    }
}