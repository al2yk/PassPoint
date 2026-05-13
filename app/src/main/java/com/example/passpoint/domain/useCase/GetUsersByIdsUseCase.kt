package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetUsersByIdsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(ids: List<String>) = repository.getUsersByIds(ids)
}