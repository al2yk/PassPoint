package com.example.passpoint.domain.utils

object EmailValidator {

    fun isValid(email: String): Boolean {
        return email.matches(Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$"))
    }

    fun getValidationError(): String {
        return "Email имеет неверный формат"
    }
}