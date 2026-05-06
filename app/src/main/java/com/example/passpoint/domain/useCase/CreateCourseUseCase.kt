package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Course
import com.example.passpoint.data.dto.CourseCreateRequest
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class CreateCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(request: CourseCreateRequest): Result<Course> {
        return repository.createCourse(request)
    }
}