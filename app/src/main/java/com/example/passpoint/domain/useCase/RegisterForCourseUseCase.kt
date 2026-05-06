package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class RegisterForCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(courseId: Int, userId: String): Result<CourseRegistration> {
        return repository.registerForCourse(courseId, userId)
    }
}