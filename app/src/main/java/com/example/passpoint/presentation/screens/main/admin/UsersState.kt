package com.example.passpoint.presentation.screens.main.admin

import com.example.passpoint.data.dto.User

data class UsersState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val searchQuery: String = "",
    val selectedRoleFilter: Int? = null,   // null = все
    val sortAlphabetically: Boolean = false, // по алфавиту имени
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteDialogUserId: String? = null,
    val roleDialogUserId: String? = null,
    val selectedRoleForDialog: Int? = null,
    val confirmRoleChange: Boolean = false, // второй шаг подтверждения
    val statsTotal: Int = 0,
    val statsByRole: Map<Int, Int> = emptyMap(),
    val statsNewThisWeek: Int = 0,
    val weeklyRegistrations: List<Int> = listOf(0, 0, 0, 0, 0),
    val monthlyRegistrations: List<Int> = listOf(0, 0, 0, 0, 0)
)