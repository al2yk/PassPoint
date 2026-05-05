package com.example.passpoint.presentation.screens.main.course

import com.example.passpoint.data.dto.User

data class CreateCourseState(
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val place: String = "",
    val selectedCurator: User? = null,
    val capacity: Int? = null,
    val curators: List<User> = emptyList(),
    val isLoadingCurators: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val showDatePicker: Boolean = false
)