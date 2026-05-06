package com.example.passpoint.presentation.screens.main.news

import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCategory

data class NewsState(
    val news: List<News> = listOf(),
    val category: List<NewsCategory> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategoryId: Int = 0
)