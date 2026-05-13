package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.CourseWithEnrollment

data class PastCoursesState(
    val courses: List<CourseWithEnrollment> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)