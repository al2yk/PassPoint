package com.example.passpoint.presentation.screens.authorization

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.passpoint.presentation.theme.BrandTonal200
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.theme.White50
import com.example.passpoint.presentation.theme.White60
import com.example.passpoint.presentation.viewModel.SingInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInView(controller: NavHostController, viewModel: SingInViewModel = hiltViewModel()) {
    val state = viewModel.state
    val isBusy = state.isLoading
    val focusManager = LocalFocusManager.current

    // Focus requesters
    val loginFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val buttonFocusRequester = remember { FocusRequester() }

    val isVisible = remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        if (isBusy) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() },
            contentPadding = PaddingValues(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                SpacerHeight(60)
                AuthTitle("Войдите в систему используя реквизиты", !isBusy)
                SpacerHeight(30)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.updatestate(state.copy(email = it, error = null)) },
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
                        enabled = !isBusy,
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

                    SpacerHeight(10)

                    // Password field
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.updatestate(state.copy(password = it, error = null)) },
                        label = {
                            Text(
                                text = "Пароль",
                                color = White50,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = MaterialTheme.shapes.small,
                        enabled = !isBusy,
                        visualTransformation = if (isVisible.value)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { isVisible.value = !isVisible.value },
                                enabled = !isBusy
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (isVisible.value)
                                            R.drawable.visibility
                                        else R.drawable.visibility_off
                                    ),
                                    contentDescription = if (isVisible.value)
                                        "" else "",
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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = {
                                controller.navigate(NavigationRoutes.CHANGEPASSWORD)
                            }
                        ) {
                            Text("Забыли пароль? ", color = if (isBusy) White50 else White60)
                        }
                    }
                    SpacerHeight(10)
                    Box(modifier = Modifier.height(56.dp)) {
                        if (!state.error.isNullOrBlank()) {
                            SpacerHeight(20)
                            WarningMessage(state.error)
                            SpacerHeight(8)
                        }
                    }
                    SpacerHeight(48)
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
                        controller.navigate(NavigationRoutes.SIGNUP)
                    },
                    modifier = Modifier
                        .height(ButtonHeight)
                        .weight(1f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "Регистрация",
                        color = if (!isBusy) White else White50
                    )
                }
                SpacerWidth(8)
                PrimaryButton(
                    onClick = {
                        viewModel.signIn(controller)
                        focusManager.clearFocus()
                    },
                    enabled = !isBusy && state.email.isNotEmpty() && state.password.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(buttonFocusRequester)
                ) {
                    Text(
                        "Войти",
                        color = if (!isBusy) BrandColor else BrandTonal200
                    )
                }
            }

        }

    }

}