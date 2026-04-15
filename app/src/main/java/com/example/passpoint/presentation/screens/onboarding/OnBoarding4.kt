package com.example.passpoint.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.R

@Composable
fun OnBoarding4(isCompact: Boolean = true) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight(if (isCompact) 0.5f else 0.6f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pic4),
                contentDescription = "",
                modifier = Modifier
                    .alpha(0.5f)
                    .offset(y = if (isCompact) 0.dp else (-50).dp),
                contentScale = ContentScale.FillHeight
            )
        }
        Text(
            "Виджет с анонсами, push‑напоминания о старте занятий",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
    }
}