package com.example.passpoint.presentation.screens.main.mine

import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.presentation.screens.main.ConfirmDialogState
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState

data class MineState(
    val registeredEvents: List<Event> = listOf(),
    val registeredCourses: List<CourseWithEnrollment> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationLoading: Boolean = false,
    val eventConfirmDialog: ConfirmDialogState? = null,
    val courseConfirmDialog: CourseConfirmDialogState? = null
)