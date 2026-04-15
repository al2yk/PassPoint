package com.example.passpoint.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.R

@Composable
fun OnBoarding3(isCompact: Boolean = true) {
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.pic3),
            contentDescription = "",
            modifier = Modifier
                .alpha(0.5f)
                .padding(top = if (isCompact) 100.dp else 0.dp)
                .fillMaxWidth(if (isCompact) 1f else 0.7f)
                .fillMaxHeight(if (isCompact) 0.4f else 0.5f)
                .then(
                    if (!isCompact) Modifier.align(Alignment.TopCenter)
                    else Modifier
                )
        )
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).fillMaxHeight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "После курса сразу получите PDF‑сертификат. Сохрани на телефон или отправь на почту",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}
