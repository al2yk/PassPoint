package com.example.passpoint.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.Gray800
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlyRegistrationsBarChart(
    monthlyCounts: List<Int>,
    modifier: Modifier = Modifier
) {
    if (monthlyCounts.isEmpty() || monthlyCounts.all { it == 0 }) {
        Text("Нет данных по месяцам", modifier = modifier, style = MaterialTheme.typography.bodyLarge)
        return
    }

    val maxCount = monthlyCounts.maxOrNull() ?: 1
    val today = LocalDate.now()
    val labels = (0..4).map { i ->
        val target = today.minusMonths((4 - i).toLong())
        target.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru")).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Регистрации по месяцам",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SpacerHeight(12)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val barCount = monthlyCounts.size
            val totalBarWidth = size.width / barCount
            val barWidth = totalBarWidth * 0.6f
            val spacing = (totalBarWidth - barWidth) / 2

            monthlyCounts.forEachIndexed { index, count ->
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