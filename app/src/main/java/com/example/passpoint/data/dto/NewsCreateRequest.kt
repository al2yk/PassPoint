package com.example.passpoint.data.dto

data class NewsCreateRequest(
    val title: String,
    val new_text: String,
    val news_category: Int,
    val create: String,
    val photo: String = ""
)