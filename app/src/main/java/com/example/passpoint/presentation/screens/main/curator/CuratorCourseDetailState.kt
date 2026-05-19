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
    val revokeConfirmDialog: RevokeConfirmDialog? = null,   // для отмены выдачи
    val isIssuingCertificate: Boolean = false,
    val certificateMessage: String? = null
)

data class ParticipantInfo(
    val user: User,
    val attendanceId: Int,
    val status: Int?,
    val certificateIssued: Boolean = false,
    val isIssuing: Boolean = false,
    val isRevoking: Boolean = false    // флаг загрузки при отмене
)

data class AttendanceConfirmDialog(
    val attendanceId: Int,
    val userName: String,
    val courseName: String,
    val newStatus: Int
)

data class RevokeConfirmDialog(
    val attendanceId: Int,
    val userId: String,
    val userName: String,
    val courseId: Int,
    val certificateUrl: String?
)