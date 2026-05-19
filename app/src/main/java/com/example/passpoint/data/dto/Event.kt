package com.example.passpoint.data.dto

data class Event(
    val id:Int,
    val created_at: String,
    val name: String,
    val date: String,
    val place: String,
    val photo: String?,
)