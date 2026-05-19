package com.example.passpoint.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.navigation.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    /**
     * 0 - Пользователь не просмотрел On Boarding
     * 1 - On Boarding просмотрен, Пользователь не авторизован - Sign In
     * 2 - Пользователь авторизован - Main
     */
    fun launch(controller: NavHostController) {
        viewModelScope.launch {
            delay(1500)
            if(UserRepository.act == 0) {
                controller.navigate(NavigationRoutes.ONBOARDING) {
                    popUpTo(NavigationRoutes.SPLASH) {
                        inclusive = true
                    }
                }
            }
            if(UserRepository.act == 1) {
                controller.navigate(NavigationRoutes.SIGNIN) {
                    popUpTo(NavigationRoutes.SPLASH) {
                        inclusive = true
                    }
                }
            }
            if(UserRepository.act == 2) {
                controller.navigate(NavigationRoutes.MAIN) {
                    popUpTo(NavigationRoutes.SPLASH) {
                        inclusive = true
                    }
                }
            }
        }
    }
}