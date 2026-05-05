package com.example.passpoint.data.dto

data class CourseCreateRequest(
    val name: String,
    val description: String,
    val date: String,
    val place: String,
    val curator: String,
    val capacity: Int
)