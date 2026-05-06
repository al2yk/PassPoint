package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Course
import com.example.passpoint.data.dto.CourseCreateRequest
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class UpdateCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(courseId: Int, request: CourseCreateRequest): Result<Course> {
        return repository.updateCourse(courseId, request)
    }
}