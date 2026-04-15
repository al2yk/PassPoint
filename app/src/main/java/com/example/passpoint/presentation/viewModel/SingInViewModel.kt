package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.SignInUseCase
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.screens.authorization.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingInViewModel @Inject constructor(private val signInUseCase: SignInUseCase) : ViewModel() {

    private val _state = mutableStateOf(SignInState())
    val state: SignInState get() = _state.value

    fun updatestate(newstate: SignInState) {
        _state.value = newstate
    }

    fun signIn(controller: NavController) {
        viewModelScope.launch {
            updatestate(state.copy(isLoading = true, error = null))
            try {
                when (val result = signInUseCase(state.email, state.password)) {
                    is Result.Failure -> {
                        updatestate(
                            state.copy(
                                isLoading = false,
                                error = "Неправильный логин или пароль"
                            )
                        )
                    }

                    is Result.Success -> {
                        updatestate(state.copy(isLoading = false))
                        UserRepository.ID = result.data.user.id.toString()
                        UserRepository.act = 2
                        controller.navigate(NavigationRoutes.MAIN) {
                            popUpTo(0)
                        }
                    }
                }
            } catch (e: Exception) {
                updatestate(state.copy(isLoading = false, error = e.message))
            }
        }
    }
}