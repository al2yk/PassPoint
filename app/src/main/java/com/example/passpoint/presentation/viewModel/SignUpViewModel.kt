package com.example.passpoint.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.SignUpUseCase
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.screens.authorization.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = mutableStateOf(SignUpState())
    val state: SignUpState get() = _state.value

    fun updatestate(newstate: SignUpState) {
        _state.value = newstate
    }

    fun signUp(controller: NavController) {
        Log.d("SignUpVM", "Before call: email=${state.email}, password=${state.password}, surname=${state.surname}, name=${state.name}")

        viewModelScope.launch {
            val validationError = validateInput(state)
            Log.e("SignUpVM", "Validation result: $validationError")
            if (validationError != null) {
                updatestate(state.copy(isLoading = false, error = validationError))
                return@launch
            }

            Log.e("SignUpVM", "Validation passed, sending request...")
            updatestate(state.copy(isLoading = true, error = null))

            try {
                when (val result = signUpUseCase(
                    email = state.email,
                    password = state.password,
                    surname = state.surname,
                    name = state.name
                )) {
                    is Result.Failure -> {
                        Log.d("UserRepo", "Raw SignUp Response: $result")
                        val exceptionMessage = result.exception.message ?: ""
                        val errorMessage = when {
                            exceptionMessage.contains("already exists", ignoreCase = true) ->
                                "Пользователь с таким логином уже существует"
                            exceptionMessage.contains("weak_password", ignoreCase = true) ->
                                "Пароль должен содержать не менее 6 символов"
                            else -> exceptionMessage.ifBlank { "Неизвестная ошибка" }
                        }
                        Log.e("SignUpVM", "Ошибка сервера: $errorMessage")
                        updatestate(state.copy(isLoading = false, error = errorMessage))
                    }
                    is Result.Success -> {
                        Log.d("SignUpVM", "Регистрация успешна")
                        updatestate(state.copy(isLoading = false))
                        UserRepository.ID = result.data.user.id.toString()
                        UserRepository.act = 3
                        controller.navigate(NavigationRoutes.SIGNIN) {
                            popUpTo(0)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SignUpVM", "Исключение: ${e.message}")
                updatestate(state.copy(isLoading = false, error = e.message ?: "Ошибка соединения"))
            }
        }
    }

    private fun validateInput(state: SignUpState): String? {
        return when {
            state.password.length < 6 -> "Пароль должен содержать не менее 6 символов"
            state.password != state.passwordTwo -> "Пароли не совпадают"
            state.name.isBlank() -> "Введите имя"
            state.surname.isBlank() -> "Введите фамилию"
            state.email.isBlank() -> "Введите логин"
            else -> null
        }
    }
}