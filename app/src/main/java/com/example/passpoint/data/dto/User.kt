package com.example.passpoint.data.dto

import java.util.UUID

data class User (
    val id: UUID,
    val user_id: UUID,
    val email: String,
    val name: String,
    val surname: String,
    val role: Int
)