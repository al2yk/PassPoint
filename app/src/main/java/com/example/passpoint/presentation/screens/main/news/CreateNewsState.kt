package com.example.passpoint.presentation.screens.main.news

import com.example.passpoint.data.dto.NewsCategory

data class CreateNewsState(
    val title: String = "",
    val text: String = "",
    val selectedCategory: NewsCategory? = null,
    val categories: List<NewsCategory> = emptyList(),
    val date: String = "",
    val isLoadingCategories: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val showDatePicker: Boolean = false,
    val imageUrl: String? = null,
    val isUploadingImage: Boolean = false
)