package com.example.passpoint.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview


val BACKGROUND_COLORS_LIGHT = arrayOf(0f to BrandColor, (299.4231f / 360f) to Color(0xFFFF1B00), 1f to BrandColor)
val BACKGROUND_COLORS_DARK = arrayOf(0f to Color(0xFF585756), (299.4231f / 360f) to Color(0xFF1D1C1A), 1f to Color(0xFF585756))
val BACKGROUND_COLORS_GREY = arrayOf(0f to Color(0xFF7D7B7A), (299.4231f / 360f) to Color(0xFF555555), 1f to Color(0xFF7D7B7A))

fun DrawScope.drawSigurBackground(vararg colors: Pair<Float, Color> = BACKGROUND_COLORS_LIGHT) {
    val offset = Offset(size.width * 0.2986f, size.height * 1.0545f)

    drawRect(
        brush = Brush.angledSweepGradient(
            colorStops = colors,
            center = offset,
            startAngle = -90f - 87f
        )
    )
}

@Composable
fun Background(
    modifier: Modifier = Modifier,
    vararg colors: Pair<Float, Color> = BACKGROUND_COLORS_LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawSigurBackground(*colors)
            },
        content = content
    )
}

@Composable
fun Background(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    Background(
        modifier,
        colors = if(isDark) BACKGROUND_COLORS_DARK else BACKGROUND_COLORS_LIGHT,
        content
    )
}

@Composable
@Preview
private fun SigurBackgroundPreview() {
    Background(
        modifier = Modifier.fillMaxSize(),
        isDark = false
    ) {
        Text("Hello World!")
    }
}
