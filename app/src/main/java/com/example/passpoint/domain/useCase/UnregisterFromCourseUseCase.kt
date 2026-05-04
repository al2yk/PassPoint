package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class UnregisterFromCourseUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(registrationId: Int) {
        repository.unregisterFromCourse(registrationId)
    }
}