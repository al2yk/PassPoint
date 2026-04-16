package com.example.passpoint.presentation.screens.authorization.changePassword.setpassword

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.AuthTitle
import com.example.passpoint.presentation.components.PrimaryButton
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.theme.White50
import com.example.passpoint.presentation.theme.White60
import com.example.passpoint.presentation.viewModel.SetPasswordViewModel

@Composable
fun SetPasswordView(
    controller: NavHostController,
    viewModel: SetPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val state = viewModel.state

    val isBusy = state.isLoading
    var isVisible by remember { mutableStateOf(false) }
    var isVisibleSecond by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        if (isBusy) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AuthTitle("Установите новый пароль для входа в систему", true)
            SpacerHeight(30)

            // Поле Пароль
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    viewModel.updateState(state.copy(password = it, errorMessage = null))
                },
                label = {
                    Text(
                        text = "Пароль",
                        color = White50,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.small,
                enabled = !isBusy,
                visualTransformation = if (isVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { isVisible = !isVisible },
                        enabled = !isBusy
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isVisible) R.drawable.visibility
                                else R.drawable.visibility_off
                            ),
                            contentDescription = if (isVisible) "" else "",
                            tint = if (isBusy) White60 else White
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = White50,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = White,
                    disabledBorderColor = White50
                )
            )
            SpacerHeight(10)

            // ПАРОЛЬ 2
            OutlinedTextField(
                value = state.passwordCheck,
                onValueChange = {
                    viewModel.updateState(state.copy(passwordCheck = it, errorMessage = null))
                },
                label = {
                    Text(
                        text = "Повторите пароль",
                        color = White50,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.small,
                enabled = !isBusy,
                visualTransformation = if (isVisibleSecond)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { isVisibleSecond = !isVisibleSecond },
                        enabled = !isBusy
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isVisibleSecond) R.drawable.visibility
                                else R.drawable.visibility_off
                            ),
                            contentDescription = if (isVisibleSecond) "" else "",
                            tint = if (isBusy) White60 else White
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = White50,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = White,
                    disabledBorderColor = White50
                )
            )
            SpacerHeight(16)
            if (state.errorMessage != null) {
                WarningMessage(text = state.errorMessage)
                SpacerHeight(8)
            }

            SpacerHeight(16)
            Spacer(modifier = Modifier.weight(1f))
            SpacerHeight(50)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
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
                        // Вызов валидации и смены пароля
                        viewModel.validateAndSetPassword(context, controller)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isBusy
                ) {
                    Text("Сохранить", color = BrandColor)
                }
            }
        }
    }
}