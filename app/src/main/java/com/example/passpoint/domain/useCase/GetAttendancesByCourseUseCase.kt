package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetAttendancesByCourseUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(courseId: Int) = repository.getAttendancesByCourse(courseId)
}