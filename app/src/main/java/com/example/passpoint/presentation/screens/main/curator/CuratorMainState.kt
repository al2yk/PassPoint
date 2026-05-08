package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.CourseWithEnrollment

data class CuratorMainState(
    val myCourses: List<CourseWithEnrollment> = listOf(),
    val filteredCourses: List<CourseWithEnrollment> = listOf(),
    val searchQuery: String = "",
    val sortAscending: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)