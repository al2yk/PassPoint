package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.User

data class CuratorCourseDetailState(
    val courseName: String = "",
    val courseDate: String = "",   // <-- новое поле
    val participants: List<ParticipantInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: AttendanceConfirmDialog? = null
)

data class ParticipantInfo(
    val user: User,
    val attendanceId: Int,
    val status: Int?   // 1-Участвует, 2-Присутствовал, 3-Отсутствовал
)

data class AttendanceConfirmDialog(
    val attendanceId: Int,
    val userName: String,
    val courseName: String,
    val newStatus: Int  // 2 или 3
)