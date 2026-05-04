package com.example.passpoint.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.useCase.GetProfileUseCase
import com.example.passpoint.presentation.theme.AppTheme
import com.example.passpoint.presentation.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.passpoint.domain.model.Result
import com.example.passpoint.presentation.screens.main.ProfileState

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<AppTheme>(AppTheme.SYSTEM)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _state = mutableStateOf(ProfileState())
    val state: ProfileState get() = _state.value

    init {
        viewModelScope.launch {
            themeManager.themeFlow.collect { theme ->
                _currentTheme.value = theme
            }
        }
        loadProfile()
    }

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            themeManager.saveTheme(theme)
        }
    }

    fun retry() {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                when (val result = getProfileUseCase.invoke(UserRepository.ID)) {
                    is Result.Failure -> {
                        val message = result.exception.message ?: "Ошибка загрузки профиля"
                        _state.value = _state.value.copy(isLoading = false, error = message)
                    }
                    is Result.Success<List<User>> -> {
                        val users = result.data
                        if (users.isEmpty()) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = "Профиль не найден"
                            )
                        } else {
                            val user = users.first()
                            _state.value = _state.value.copy(
                                name = user.name,
                                surname = user.surname,
                                email = user.email,
                                photo = user.photo,
                                role = user.role,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Ошибка загрузки профиля", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
}