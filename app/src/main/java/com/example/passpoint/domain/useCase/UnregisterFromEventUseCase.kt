package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class UnregisterFromEventUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(registrationId: Long?) {
        repository.unregisterFromEvent(registrationId)
    }
}