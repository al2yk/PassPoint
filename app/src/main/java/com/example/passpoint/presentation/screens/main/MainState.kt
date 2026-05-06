package com.example.passpoint.presentation.screens.main

import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.User

data class MainState(
    val news: List<News> = listOf(),
    val events: List<Event> = listOf(),
    val curators: List<User> = listOf(),
    val course: List<CourseWithEnrollment> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrations: List<EventRegistration> = listOf(),
    val isRegistrationLoading: Boolean = false,
    val confirmDialog: ConfirmDialogState? = null,
    val courseRegistrations: List<CourseRegistration> = listOf(),
    val courseConfirmDialog: CourseConfirmDialogState? = null
)

data class ConfirmDialogState(
    val eventId: Int,
    val action: ConfirmAction
)