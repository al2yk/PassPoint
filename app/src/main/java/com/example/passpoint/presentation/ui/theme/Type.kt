package com.example.passpoint.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.passpoint.R

val DinProMedium = FontFamily(Font(R.font.dinpro_medium))
val DinProRegular = FontFamily(Font(R.font.dinpro_regular))
val RobotoMedium = FontFamily(Font(R.font.roboto_medium))
val RobotoRegular = FontFamily(Font(R.font.roboto_regular))

val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontFamily = DinProMedium
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontFamily = DinProMedium
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        fontFamily = DinProMedium,
    ),
    titleLarge = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontFamily = DinProMedium
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = RobotoMedium
    ),
    titleSmall = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontFamily = RobotoMedium
    ),

    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = RobotoRegular,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = DinProMedium
    ),
    labelMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = RobotoMedium
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFamily = RobotoMedium
    ),
    displaySmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18 .sp,
        fontFamily = RobotoRegular,
        letterSpacing = 0.25.sp
    ),
    displayMedium = TextStyle(
        fontSize = 36.sp,
        lineHeight = 27.sp,
        fontFamily = RobotoMedium,
        letterSpacing = 0.sp
    )
)
