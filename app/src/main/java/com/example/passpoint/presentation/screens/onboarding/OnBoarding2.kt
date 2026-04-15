package com.example.passpoint.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.passpoint.R

@Composable
fun OnBoarding2(isCompact: Boolean = true) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Покажите код на входе — посещаемость отметится автоматически",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .then(
                    if (isCompact) Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                    else Modifier
                        .fillMaxWidth(0.7f)
                ),
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.pic2),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.5f)
                .rotate(-20f)
                .then(
                    if (isCompact) Modifier
                        .offset(y = 50.dp)
                        .size(500.dp)
                    else Modifier
                        .offset(y = (-25).dp)
                        .fillMaxHeight(0.7f)

                ),
            contentScale = ContentScale.FillHeight
        )
    }
}
