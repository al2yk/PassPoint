package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.NewPasswordResponse
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class SetPasswordUseCase @Inject constructor
    (private val userRepository: Repository) {
    suspend operator fun invoke(email: String, password: String): Result<NewPasswordResponse> {
        return userRepository.newPassword(email, password)
    }
}