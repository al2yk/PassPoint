package com.example.passpoint.data.dto

import androidx.compose.runtime.Immutable

@Immutable
data class Course(
    val id: Int,
    val created_at: String,
    val name: String,
    val description: String,
    val date: String,
    val place: String,
    val photo: String?,
    val curator: String?,
    val capacity: Int
)