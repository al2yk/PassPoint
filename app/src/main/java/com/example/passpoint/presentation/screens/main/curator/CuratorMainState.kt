package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.CourseWithEnrollment

enum class CuratorCourseSortType {
    NAME_ASC,   // А-Я
    NAME_DESC,  // Я-А
    DATE_ASC,   // Дата ↑ (старые сначала)
    DATE_DESC   // Дата ↓ (новые сначала)
}

data class CuratorMainState(
    val myCourses: List<CourseWithEnrollment> = listOf(),
    val filteredCourses: List<CourseWithEnrollment> = listOf(),
    val searchQuery: String = "",
    val sortType: CuratorCourseSortType = CuratorCourseSortType.DATE_ASC,
    val isLoading: Boolean = false,
    val error: String? = null
)