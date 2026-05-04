package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetUserEventRegistrationsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(userId: String): Result<List<EventRegistration>> {
        return repository.getUserRegistrations(userId)
    }
}