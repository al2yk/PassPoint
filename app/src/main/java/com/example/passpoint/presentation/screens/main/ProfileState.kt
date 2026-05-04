package com.example.passpoint.presentation.screens.main

data class ProfileState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val photo: String = "",
    val role: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null
)