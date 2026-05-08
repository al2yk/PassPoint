package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetCourseByIdUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(courseId: Int): Result<CourseWithEnrollment> =
        repository.getCourseById(courseId)
}