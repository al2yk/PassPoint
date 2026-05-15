package com.example.passpoint.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.ui.theme.Green500
import com.example.passpoint.presentation.ui.theme.Yellow500

@Composable
fun UsersPieChart(
    participants: Int,
    curators: Int,
    admins: Int,
    modifier: Modifier = Modifier
) {
    val total = participants + curators + admins
    val participantsFraction = if (total > 0) participants.toFloat() / total else 0f
    val curatorsFraction = if (total > 0) curators.toFloat() / total else 0f
    val adminsFraction = if (total > 0) admins.toFloat() / total else 0f
    val participantsAngle = participantsFraction * 360f
    val curatorsAngle = curatorsFraction * 360f
    val adminsAngle = adminsFraction * 360f

    if (total > 0) {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.width(240.dp), contentAlignment = Alignment.Center) {

                Column(
                    modifier = modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        val strokeWidth = 35f
                        val radius = size.minDimension / 2 - strokeWidth / 2
                        val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
                        val arcSize = Size(radius * 2, radius * 2)

                        // Фон
                        drawArc(
                            color = Color(0xFFE0E0E0),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )

                        // Участники (BrandColor)
                        drawArc(
                            color = BrandColor,
                            startAngle = -90f,
                            sweepAngle = participantsAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Кураторы (оранжевый)
                        drawArc(
                            color = Yellow500,
                            startAngle = -90f + participantsAngle,
                            sweepAngle = curatorsAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Администраторы (зелёный)
                        drawArc(
                            color = Green500,
                            startAngle = -90f + participantsAngle + curatorsAngle,
                            sweepAngle = adminsAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    SpacerHeight(12)

                }
            }
            SpacerWidth(8)
            Column(
                modifier = Modifier.fillMaxWidth().offset(y = (-20).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SpacerHeight(16)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(color = BrandColor)
                        }
                        SpacerWidth(5)
                        Text(
                            text = "${participants}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BrandColor
                        )
                        Text(
                            text = "(${(participantsFraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = BrandColor
                        )
                    }
                    Text(
                        text = "Участники",
                        style = MaterialTheme.typography.displaySmall,
                        color = Gray800
                    )
                }
                SpacerHeight(12)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(color = Yellow500)
                        }
                        SpacerWidth(5)
                        Text(
                            text = "${curators}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Yellow500
                        )
                        Text(
                            text = "(${(curatorsFraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Yellow500
                        )
                    }
                    Text(
                        text = "Кураторы",
                        style = MaterialTheme.typography.displaySmall,
                        color = Gray800
                    )
                }
                SpacerHeight(12)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(color = Green500)
                        }
                        SpacerWidth(5)
                        Text(
                            text = "${admins}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Green500
                        )
                        Text(
                            text = "(${(adminsFraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Green500
                        )
                    }
                    Text(
                        text = "Администраторы",
                        style = MaterialTheme.typography.displaySmall,
                        color = Gray800
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${admins+curators+participants}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "Всего",
                        style = MaterialTheme.typography.displaySmall,
                        color = Gray800
                    )
                }
            }
        }
    } else {
        Text("Нет данных", modifier = modifier, style = MaterialTheme.typography.bodyLarge)
    }
}
