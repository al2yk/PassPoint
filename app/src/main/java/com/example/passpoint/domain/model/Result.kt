package com.example.passpoint.domain.model

sealed class Result<out T> {

    // Успешный результат с данными
    data class Success<out T>(val data: T) : Result<T>()

    // Неуспешный результат с исключением
    data class Failure(val exception: Exception) : Result<Nothing>()
}