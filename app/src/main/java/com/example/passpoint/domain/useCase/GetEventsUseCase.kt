package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Event
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val userRepository: Repository
) {
    suspend operator fun invoke(): Result<List<Event>> {
        return userRepository.getEvent()
    }
}