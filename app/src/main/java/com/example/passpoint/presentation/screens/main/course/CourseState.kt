package com.example.passpoint.presentation.screens.main.course

import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.User
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState

enum class CourseSortType {
    NAME_ASC,   // А-Я
    NAME_DESC,  // Я-А
    DATE_ASC,   // Дата ↑ (старые сначала)
    DATE_DESC   // Дата ↓ (новые сначала) - по умолчанию
}

data class CoursesState(
    val upcomingCourses: List<CourseWithEnrollment> = listOf(),
    val pastCourses: List<CourseWithEnrollment> = listOf(),
    val isShowingPast: Boolean = false,
    val registrations: List<CourseRegistration> = listOf(),
    val isLoading: Boolean = false,
    val isRegistrationLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: CourseConfirmDialogState? = null,
    val deleteDialog: CourseConfirmDialogState? = null,
    val curators: List<User> = listOf(),
    val searchQuery: String = "",
    val sortType: CourseSortType = CourseSortType.DATE_ASC,
    val filteredUpcomingCourses: List<CourseWithEnrollment> = listOf(),
    val filteredPastCourses: List<CourseWithEnrollment> = listOf()
)