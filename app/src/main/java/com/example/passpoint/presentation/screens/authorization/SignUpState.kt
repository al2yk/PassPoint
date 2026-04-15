package com.example.passpoint.presentation.screens.authorization

data class SignUpState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordTwo: String = "",
    val checkBox: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)