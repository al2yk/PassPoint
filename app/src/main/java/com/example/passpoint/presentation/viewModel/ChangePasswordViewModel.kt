package com.example.passpoint.presentation.viewModel

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.SendOtpUseCase
import com.example.passpoint.presentation.screens.authorization.changePassword.ChangePasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val sendOtp: SendOtpUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(ChangePasswordState())
    val state: ChangePasswordState get() = _state.value

    fun updatestate(newstate: ChangePasswordState) {
        _state.value = newstate
    }

    fun forgotPasswordOTP(context: Context, email: String? = null) {
        val e = if (email != null) email else state.email
        viewModelScope.launch {
            if (e.isEmailValid()) {
                updatestate(_state.value.copy(dialog = true))
                Log.e("Огонь email", "${state.email} ${UserRepository.email}")
                UserRepository.email = state.email
                Log.e("Огонь email", "${state.email} ${UserRepository.email}")
                when (val result = sendOtp.invoke(e)) {
                    is Result.Failure -> {
                        Log.e("fail", result.toString())
                        Toast.makeText(
                            context,
                            "Ошибка отправки письма. Попробуйте позже",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Success<*> -> {
                        Log.e("Огонь email", "${state.email} ${UserRepository.email}")
                        UserRepository.email = state.email
                        Log.e("Огонь email", "${state.email} ${UserRepository.email}")
                    }
                }

            } else {
                Log.d("forgot password", "плохо")
                Toast.makeText(context, "Неверный формат почты", Toast.LENGTH_SHORT).show()
            }

        }
    }
}