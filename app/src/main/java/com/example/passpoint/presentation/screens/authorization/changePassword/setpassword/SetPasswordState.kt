package com.example.passpoint.presentation.screens.authorization.changePassword.setpassword

data class SetPasswordState(
    val password: String = "",
    val passwordCheck: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)