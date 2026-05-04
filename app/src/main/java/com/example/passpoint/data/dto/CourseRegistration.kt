package com.example.passpoint.data.dto

data class CourseRegistration(
    val id: Int? = null,
    val created_at: String? = null,
    val user: String,
    val course: Int,
    val status: Int? = null
)