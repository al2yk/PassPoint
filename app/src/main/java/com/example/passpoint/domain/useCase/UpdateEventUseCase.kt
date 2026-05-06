package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventCreateRequest
import com.example.passpoint.domain.repository.Repository
import jakarta.inject.Inject
import com.example.passpoint.domain.model.Result


class UpdateEventUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(eventId: Int, request: EventCreateRequest): Result<Event> =
        repository.updateEvent(eventId, request)
}