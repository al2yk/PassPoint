package com.example.passpoint.data.dto


data class CourseWithEnrollment (
    val id: Int,
    val created_at: String,
    val name: String,
    val description: String,
    val date: String,
    val place: String,
    val curator: String,
    val capacity: Int,
    val enrolled_count: Int
)