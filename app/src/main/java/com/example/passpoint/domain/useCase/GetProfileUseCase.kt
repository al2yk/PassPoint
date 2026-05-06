package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class GetProfileUseCase @Inject constructor
    (private val userRepository: Repository) {
    suspend operator fun invoke(userId: String): Result<List<User>> {
        return userRepository.getProfile(userId)
    }
}