package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(): Result<List<CourseWithEnrollment>> {
        return repository.getCourse()
    }
}