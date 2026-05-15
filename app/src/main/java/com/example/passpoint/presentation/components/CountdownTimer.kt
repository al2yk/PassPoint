package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.ChangePasswordViewModel
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    initialTimeInSeconds: Int = 60,
    email: String,
    onResend: () -> Unit = {}   // new callback
) {
    var currentTime by remember { mutableStateOf(initialTimeInSeconds) }
    val formattedTime = String.format("%02d:%02d", currentTime / 60, currentTime % 60)
    val isTimerRunning = currentTime > 0
    val context = LocalContext.current

    LaunchedEffect(key1 = currentTime) {
        if (isTimerRunning) {
            delay(1000L)
            currentTime--
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = {
                if (!isTimerRunning) {
                    viewModel.forgotPasswordOTP(context, email)
                    currentTime = initialTimeInSeconds
                    onResend()
                }
            },
            enabled = !isTimerRunning
        ) {
            Text(
                "Отправить заново",
                color = if (!isTimerRunning) White else MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = White
        )
    }
}