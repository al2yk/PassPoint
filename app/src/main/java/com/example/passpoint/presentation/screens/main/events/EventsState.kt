package com.example.passpoint.presentation.screens.main.events

import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.presentation.screens.main.ConfirmDialogState

data class EventsState(
    val upcomingEvents: List<Event> = listOf(),
    val pastEvents: List<Event> = listOf(),
    val isShowingPast: Boolean = false,
    val registrations: List<EventRegistration> = listOf(),
    val isLoading: Boolean = false,
    val isRegistrationLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: ConfirmDialogState? = null
)