package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class GetAllUsersUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Result<List<User>> = repository.getAllUsers()
}