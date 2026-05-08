package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class DeleteUserUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.deleteUser(userId)
}