package com.example.passpoint.presentation.screens.main.events

data class CreateEventState(
    val name: String = "",
    val date: String = "",
    val place: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val showDatePicker: Boolean = false
)