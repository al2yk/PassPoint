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
import kotlin.toString

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val getProfilee: GetProfileUseCase
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<AppTheme>(AppTheme.SYSTEM)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _state = mutableStateOf(ProfileState())
    val state: ProfileState get() = _state.value

    fun updatestate(newstate: ProfileState) {
        _state.value = newstate
    }
    init {
        viewModelScope.launch {
            themeManager.themeFlow.collect { theme ->
                _currentTheme.value = theme
            }
        }
    }

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            themeManager.saveTheme(theme)
        }
    }
    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {
            try {
                when (val result = getProfilee.invoke(UserRepository.ID)) {
                    is Result.Failure -> {
                        Log.e("fail", result.toString())
                    }

                    is Result.Success<List<User>> -> {
                        val user = result.data[0]

                        _state.value = _state.value.copy(
                            name = user.name,
                            surname = user.surname,
                            email = user.email,
                            photo = user.photo,
                            role = user.role
                        )
                        Log.e("Огонь", "получен профиль")
                    }
                }
            } catch (e: Exception) {
                Log.d("не получен профиль", e.toString())
            }
        }
    }
}