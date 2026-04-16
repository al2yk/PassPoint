package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.VerifyOtpUseCase
import com.example.passpoint.presentation.navigation.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OTPCheckViewModel @Inject constructor(
    private val verifyOtp: VerifyOtpUseCase
) : ViewModel() {

    private var _isError by mutableStateOf(false)
    val isError: Boolean get() = _isError

    private var _warningMessage by mutableStateOf<String?>(null)
    val warningMessage: String? get() = _warningMessage

    private var _resetCounter by mutableStateOf(0)
    val resetCounter: Int get() = _resetCounter

    fun triggerReset() {
        _resetCounter++
        clearError()  // очищаем ошибку при перезапуске кода
    }

    fun clearError() {
        _isError = false
        _warningMessage = null
    }

    private fun setError() {
        _isError = true
        _warningMessage = "Неверный код подтверждения"
        // Нет автоматического сброса – ошибка будет висеть до вызова clearError()
    }

    fun checkOtpCode(email: String, code: String, controller: NavHostController) {
        viewModelScope.launch {
            when (val result = verifyOtp.invoke(email, code)) {
                is Result.Failure -> {
                    setError()
                }
                is Result.Success<*> -> {
                    controller.navigate(NavigationRoutes.SETPASSWORD) {
                        popUpTo(NavigationRoutes.OTP) { inclusive = true }
                    }
                }
            }
        }
    }
}