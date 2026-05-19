package com.example.passpoint.presentation.screens.main.events

import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.presentation.screens.main.ConfirmDialogState

enum class EventSortType {
    NAME_ASC,   // А-Я
    NAME_DESC,  // Я-А
    DATE_DESC,  // Дата ↑ (новые сначала) - по умолчанию
    DATE_ASC    // Дата ↓ (старые сначала)
}

data class EventsState(
    val upcomingEvents: List<Event> = listOf(),
    val pastEvents: List<Event> = listOf(),
    val isShowingPast: Boolean = false,
    val registrations: List<EventRegistration> = listOf(),
    val isLoading: Boolean = false,
    val isRegistrationLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: ConfirmDialogState? = null,
    val deleteDialog: ConfirmDialogState? = null,
    val searchQuery: String = "",
    val sortType: EventSortType = EventSortType.DATE_ASC,
    val filteredUpcomingEvents: List<Event> = listOf(),
    val filteredPastEvents: List<Event> = listOf()
)