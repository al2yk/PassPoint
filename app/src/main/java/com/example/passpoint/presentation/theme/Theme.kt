package com.example.passpoint.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val WhiteColorScheme = lightColorScheme(
    // Button Color; leading icon in the chips;
    primary = BrandColor,
    // Text in button
    onPrimary = White,

    background = White,
    onBackground = Black900,

    // Elevated Card Container; ModalSheet!!!
    surfaceContainerLow = White,
    // ModalBottomSheet container; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    surfaceContainerLowest = Gray300,

    // EmployeeDialog; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    surfaceContainerHighest = Gray300,
    //Dialog
    surfaceContainerHigh = White,
    // Dialog title
    onSurface = Black900,

    // FilledTonalIconButton container
    secondaryContainer = White,
    // FilledTonalIconButton icon
    onSecondaryContainer = Black850,

    // OutlinedButton container
    outlineVariant = Gray500,
    // OutlinedButton title|icon  && dragHandle ModalSheet(but I need Gray500)
    onSurfaceVariant = Black900,

    // Assist chip.
    // Assist chip title (only warning) ----->
    onTertiary = Brown900,
    // Assist chip icon; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    onTertiaryFixedVariant = Brown800,
    // Assist chip background; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    tertiaryFixed = Yellow100,
    // Assist chip icon (only error) ----->
    tertiaryFixedDim = Red800,
    // Assist chip title
    tertiaryContainer = Red900,
    // Assist chip background
    onTertiaryFixed = Red50,


    // Radio Button unselected; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    onPrimaryFixedVariant = Gray300,

    // Slider inactive track; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    inverseOnSurface = Gray400,

    // SeeAllSelectedDevicesModalSheet SN
    surfaceTint = Gray600,
    //Divider
    surfaceDim = Gray450,
    // arrow right go to action
    inverseSurface = Gray800,
    onTertiaryContainer = White50,
    outline = White70,
    surfaceBright = Brand50,
    surfaceVariant = BrandColor,
    onErrorContainer = BrandColor,
    inversePrimary = Brand50
)
val DarkColorScheme = lightColorScheme(
    // Button Color; leading icon in the chips;
    primary = BrandColor,
    // Text in button
    onPrimary = White,

    background = Black900,
    onBackground = White,

    // Elevated Card Container; ModalSheet!!!
    surfaceContainerLow = Black900,
    // ModalBottomSheet container; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    surfaceContainerLowest = Black,

    // EmployeeDialog; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    surfaceContainerHighest = Black,
    //Dialog
    surfaceContainerHigh = Black900,
    //Dialog title
    onSurface = White,

    // FilledTonalIconButton container (employee data поменять)
    secondaryContainer = Black900,
    // FilledTonalIconButton icon
    onSecondaryContainer = White,

    // OutlinedButton container
    outlineVariant = Gray500,
    // OutlinedButton title|icon (radio button, unselected)
    onSurfaceVariant = White,


    // Assist chip.
    // Assist chip title (only warning) ----->
    onTertiary = Yellow200,
    // Assist chip icon; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    onTertiaryFixedVariant = Yellow500,
    // Assist chip background; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    tertiaryFixed = Brown900,
    // Assist chip icon (only error) ----->
    tertiaryFixedDim = Red500,
    // Assist chip title
    tertiaryContainer = Red300,
    // Assist chip background
    onTertiaryFixed = Red900,

    // Radio Button unselected; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    onPrimaryFixedVariant = Gray900,

    // Slider inactive track; ИСПОЛЬЗУЮ РАНДОМНЫЙ
    inverseOnSurface = Gray400,
    // SeeAllSelectedDevicesModalSheet SN
    surfaceTint = Gray600,
    //Divider
    surfaceDim = Gray450,
    // arrow right go to action
    inverseSurface = Gray350,
    onTertiaryContainer = White50,
    outline = White70,
    surfaceBright = Gray400,
    surfaceVariant = Black900,
    onErrorContainer = White,
    inversePrimary = Brand50


)
val ButtonHeight = 48.dp


@Composable
fun PassPointTheme(
    selectedTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val darkTheme = when (selectedTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else WhiteColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}