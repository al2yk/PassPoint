package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import jakarta.inject.Inject
import com.example.passpoint.domain.model.Result

class DeleteCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(courseId: Int): Result<Unit> {
        return repository.deleteCourse(courseId)
    }
}