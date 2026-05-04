package com.example.passpoint.presentation.screens.main.events

import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration

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

data class ConfirmDialogState(
    val eventId: Int,
    val action: ConfirmAction
)

enum class ConfirmAction {
    REGISTER, UNREGISTER
}