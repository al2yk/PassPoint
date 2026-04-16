package com.example.passpoint.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.SetPasswordUseCase
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.screens.authorization.changePassword.setpassword.SetPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    private val newPass: SetPasswordUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(SetPasswordState())
    val state: SetPasswordState get() = _state.value

    fun updateState(newState: SetPasswordState) {
        _state.value = newState
    }

    fun validateAndSetPassword(context: Context, controller: NavHostController) {
        val currentState = state

        if (currentState.password.length < 6) {
            updateState(currentState.copy(errorMessage = "Пароль должен содержать не менее 6 символов"))
            return
        }

        if (currentState.password != currentState.passwordCheck) {
            updateState(currentState.copy(errorMessage = "Пароли не совпадают"))
            return
        }

        updateState(currentState.copy(errorMessage = null, isLoading = true))

        viewModelScope.launch {
            when (val result = newPass(email = UserRepository.email, password = currentState.password)) {
                is Result.Failure -> {
                    Log.e("SetPassword", "Ошибка: ${result.exception}")
                    val errorMessage = parseErrorMessage(result.exception)
                    updateState(
                        state.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    )
                }
                is Result.Success<*> -> {
                    Log.d("SetPassword", "Пароль успешно изменён")
                    controller.navigate(NavigationRoutes.SIGNIN) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    private fun parseErrorMessage(exception: Throwable): String {
        val message = exception.message ?: return "Неизвестная ошибка"
        return when {
            // Проверяем наличие кода same_password в JSON-строке
            message.contains("same_password") -> "Новый пароль должен отличаться от старого"
            message.contains("weak_password") -> "Пароль слишком простой. Придумайте более сложный"
            message.contains("HTTP 422") -> "Новый пароль должен отличаться от старого"
            else -> "Ошибка изменения пароля. Попробуйте снова"
        }
    }

    fun clearError() {
        if (state.errorMessage != null) {
            updateState(state.copy(errorMessage = null))
        }
    }
}