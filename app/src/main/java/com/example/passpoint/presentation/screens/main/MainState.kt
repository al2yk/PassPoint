package com.example.passpoint.presentation.screens.main

import com.example.passpoint.data.dto.News

data class MainState (
    val news: List<News> = listOf()
)