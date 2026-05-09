package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.User

data class CuratorUsersState(
    val users: List<CuratorUserInfo> = emptyList(),
    val filteredUsers: List<CuratorUserInfo> = emptyList(),
    val searchQuery: String = "",
    val sortAscending: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CuratorUserInfo(
    val user: User,
    val totalEnrollments: Int,
    val attended: Int,
    val missed: Int
)