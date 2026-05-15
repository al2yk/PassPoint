package com.example.passpoint.presentation.screens.authorization.changePassword.otp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.AuthTitle
import com.example.passpoint.presentation.components.CountdownTimer
import com.example.passpoint.presentation.components.InputCode
import com.example.passpoint.presentation.components.PrimaryButton
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.ui.theme.Background
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.OTPCheckViewModel

@Composable
fun OTPVIew(
    controller: NavHostController,
    viewModel: OTPCheckViewModel = hiltViewModel()
) {
    var enteredCode by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(viewModel.isError) {
        if (viewModel.isError) {
            scrollState.animateScrollTo(0)
        }
    }
    Background(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                AuthTitle("Введите полученный код подтверждения", true)
                SpacerHeight(30)

                InputCode(
                    isError = false,
                    onCodeChanged = { code ->
                        enteredCode = code
                        viewModel.clearError()
                    },
                    onCodeEntered = { },
                    submitOnComplete = false,
                    resetTrigger = viewModel.resetCounter,
                    initialFocusRequester = focusRequester
                )

                SpacerHeight(8)

                if (viewModel.warningMessage != null) {
                    WarningMessage(text = viewModel.warningMessage!!)
                    SpacerHeight(8)
                }

                SpacerHeight(16)

                CountdownTimer(
                    email = UserRepository.email,
                    onResend = {
                        viewModel.triggerReset()
                        enteredCode = ""
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                SpacerHeight(50)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { controller.navigate(NavigationRoutes.CHANGEPASSWORD) },
                        modifier = Modifier
                            .height(ButtonHeight)
                            .weight(1f),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text("Назад", color = White)
                    }
                    SpacerWidth(8)
                    PrimaryButton(
                        onClick = {
                            if (enteredCode.length == 6) {
                                keyboardController?.hide()
                                viewModel.checkOtpCode(
                                    UserRepository.email,
                                    enteredCode,
                                    controller
                                )
                            } else {
                                viewModel.clearError()

                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Проверить", color = BrandColor)
                    }
                }
            }
        }
    }
}