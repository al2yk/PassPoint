package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerWidth(dp: Int) {
    Spacer(modifier = Modifier.width(dp.dp))
}