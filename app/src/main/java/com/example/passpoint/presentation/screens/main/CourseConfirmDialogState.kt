package com.example.passpoint.presentation.screens.main

data class CourseConfirmDialogState(
    val courseId: Int,
    val action: ConfirmAction
)

enum class ConfirmAction {
    REGISTER, UNREGISTER,DELETE
}