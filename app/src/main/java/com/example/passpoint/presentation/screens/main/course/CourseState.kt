package com.example.passpoint.presentation.screens.main.course

import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState

data class CoursesState(
    val upcomingCourses: List<CourseWithEnrollment> = listOf(),
    val pastCourses: List<CourseWithEnrollment> = listOf(),
    val isShowingPast: Boolean = false,
    val registrations: List<CourseRegistration> = listOf(),
    val isLoading: Boolean = false,
    val isRegistrationLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: CourseConfirmDialogState? = null
)
