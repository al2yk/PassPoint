package com.example.passpoint.data.dto

import java.util.UUID

data class AuthResponse (
    val access_token: String,
    val user: AuthUserDto
)
data class AuthUserDto(
    val id: UUID,
    val email: String,
    val role: String   // "authenticated"
)