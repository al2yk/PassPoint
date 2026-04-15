package com.example.passpoint.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingPagingViewModel() : ViewModel() {

    private val _currentScreen = MutableStateFlow(OnBoardingEnum.ONBOARDING1)
    val currentScreen: StateFlow<OnBoardingEnum> = _currentScreen.asStateFlow()

    /**
     * Этот метод вызывается нажатием кнопки "Далее" на OnBoarding.
     * Осуществляется переход на следующий экран OnBoarding.
     */
    fun clickNextOnBoarding() {
        val nextScreen = when (currentScreen.value) {
            OnBoardingEnum.ONBOARDING1 -> OnBoardingEnum.ONBOARDING2
            OnBoardingEnum.ONBOARDING2 -> OnBoardingEnum.ONBOARDING3
            OnBoardingEnum.ONBOARDING3 -> OnBoardingEnum.ONBOARDING4
            OnBoardingEnum.ONBOARDING4 -> OnBoardingEnum.ONBOARDING4
        }
        _currentScreen.value = nextScreen
    }

    /**
     * Этот метод вызывается, когда происходит нажатие на
     * системную кнопку "назад" на OnBoarding. Или системный
     * свайп "назад"
     */
    fun clickBackOnBoarding() {
        val previousScreen = when (currentScreen.value) {
            OnBoardingEnum.ONBOARDING1 -> OnBoardingEnum.ONBOARDING1
            OnBoardingEnum.ONBOARDING2 -> OnBoardingEnum.ONBOARDING1
            OnBoardingEnum.ONBOARDING3 -> OnBoardingEnum.ONBOARDING2
            OnBoardingEnum.ONBOARDING4 -> OnBoardingEnum.ONBOARDING3
        }
        _currentScreen.value = previousScreen
    }

    /**
     * Установить текущий шаг для свайпов
     */
    fun setCurrentStep(step: Int) {
        val targetScreen = when (step) {
            0 -> OnBoardingEnum.ONBOARDING1
            1 -> OnBoardingEnum.ONBOARDING2
            2 -> OnBoardingEnum.ONBOARDING3
            3 -> OnBoardingEnum.ONBOARDING4
            else -> OnBoardingEnum.ONBOARDING1
        }
        _currentScreen.value = targetScreen
    }
}