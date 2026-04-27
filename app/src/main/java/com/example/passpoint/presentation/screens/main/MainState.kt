package com.example.passpoint.presentation.screens.main

import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.User

data class MainState(
    val news: List<News> = listOf(),
    val curators: List<User> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)