package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class RegisterForEventUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(eventId: Int, userId: String): Result<EventRegistration> {
        return repository.registerForEvent(eventId, userId)
    }
}