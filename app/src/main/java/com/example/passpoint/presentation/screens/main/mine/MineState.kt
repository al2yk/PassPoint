package com.example.passpoint.presentation.screens.main.mine

import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.presentation.screens.main.ConfirmDialogState
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState

enum class RegisteredSortType {
    NAME_ASC,   // А-Я
    NAME_DESC,  // Я-А
    DATE_ASC,   // Дата ↑ (старые сначала)
    DATE_DESC   // Дата ↓ (новые сначала)
}

data class MineState(
    val registeredEvents: List<Event> = listOf(),
    val registeredCourses: List<CourseWithEnrollment> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationLoading: Boolean = false,
    val eventConfirmDialog: ConfirmDialogState? = null,
    val courseConfirmDialog: CourseConfirmDialogState? = null,
    val searchQuery: String = "",
    val sortType: RegisteredSortType = RegisteredSortType.DATE_DESC,
    val filteredEvents: List<Event> = listOf(),
    val filteredCourses: List<CourseWithEnrollment> = listOf()
)