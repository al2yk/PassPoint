package com.example.passpoint.data.dto

data class VerifyOTPdto (
    val type: String = "email",
    val email: String = "",
    val token: String = "",
)