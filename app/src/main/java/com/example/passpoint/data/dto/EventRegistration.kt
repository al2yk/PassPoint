package com.example.passpoint.data.dto

import java.util.UUID

data class EventRegistration(
    val id: Long? = null,
    val user: String,
    val event: Int
)