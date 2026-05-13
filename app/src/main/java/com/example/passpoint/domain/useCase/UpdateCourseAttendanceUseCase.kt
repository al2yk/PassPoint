package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class UpdateCourseAttendanceUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(attendanceId: Int, newStatus: Int) =
        repository.updateCourseAttendance(attendanceId, newStatus)
}