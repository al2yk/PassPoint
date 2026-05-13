package com.example.passpoint.presentation.screens.main.curator

import com.example.passpoint.data.dto.User

data class CuratorCourseDetailState(
    val courseName: String = "",
    val courseDate: String = "",
    val courseId: Int? = null,
    val participants: List<ParticipantInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val confirmDialog: AttendanceConfirmDialog? = null,
    val isIssuingCertificate: Boolean = false,     // новое
    val certificateMessage: String? = null         // новое (для уведомления)
)

data class ParticipantInfo(
    val user: User,
    val attendanceId: Int,
    val status: Int?,
    val certificateIssued: Boolean = false,
    val isIssuing: Boolean = false
)

data class AttendanceConfirmDialog(
    val attendanceId: Int,
    val userName: String,
    val courseName: String,
    val newStatus: Int  // 2 или 3
)