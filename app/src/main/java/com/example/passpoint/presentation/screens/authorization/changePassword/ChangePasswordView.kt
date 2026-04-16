package com.example.passpoint.presentation.screens.authorization.changePassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.presentation.components.AuthTitle
import com.example.passpoint.presentation.components.OTPSendDialog
import com.example.passpoint.presentation.components.PrimaryButton
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.theme.White50
import com.example.passpoint.presentation.viewModel.ChangePasswordViewModel

@Composable
fun ChangePasswordView(controller: NavHostController, viewModel: ChangePasswordViewModel = hiltViewModel()) {
    val state = viewModel.state

    val focusManager = LocalFocusManager.current
    val loginFocusRequester = remember { FocusRequester() }

    // Показываем диалог при успешной отправке
    if (state.dialog) {
        OTPSendDialog(
            onDismiss = {
                // Закрываем диалог и переходим на экран ввода OTP
                viewModel.updatestate(state.copy(dialog = false))
                controller.navigate(NavigationRoutes.OTP)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() },
            contentPadding = PaddingValues(bottom = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                SpacerHeight(280)
                AuthTitle("Введите свой логин для сброса пароля", true)
                SpacerHeight(30)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.updatestate(state.copy(email = it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(loginFocusRequester),
                        label = {
                            Text(
                                text = "Логин",
                                style = MaterialTheme.typography.bodyLarge,
                                color = White50
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        enabled = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = White50,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = White,
                            disabledBorderColor = White50
                        )
                    )
                }
            }
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
                    onClick = {
                        controller.navigate(NavigationRoutes.SIGNIN)
                    },
                    modifier = Modifier
                        .height(ButtonHeight)
                        .weight(1f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "Назад",
                        color = White
                    )
                }
                SpacerWidth(8)
                val context = LocalContext.current

                PrimaryButton(
                    onClick = {
                        viewModel.forgotPasswordOTP(context)
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        "Отправить",
                        color = BrandColor
                    )
                }
            }
        }
    }
}