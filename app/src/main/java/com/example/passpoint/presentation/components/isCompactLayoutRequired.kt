package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable

@Composable
fun BoxWithConstraintsScope.isCompactLayoutRequired(): Boolean {
    return maxWidth < maxHeight
}