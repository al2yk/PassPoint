package com.example.passpoint.domain.model

import java.util.UUID

data class AuthUserDomain(
    val id: UUID,
    val email: String
)