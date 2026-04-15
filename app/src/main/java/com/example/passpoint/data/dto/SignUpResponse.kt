package com.example.passpoint.data.dto

import java.util.UUID

// SignUpResponse.kt
data class SignUpResponse(
    val id: UUID?,   // теперь может быть null
    val email: String?,
    val role: String? = null
)