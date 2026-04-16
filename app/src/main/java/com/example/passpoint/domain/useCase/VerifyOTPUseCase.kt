package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.VerifyOTPResponse
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.model.Result
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor
    (private val userRepository: Repository) {
    suspend operator fun invoke(email: String, token: String): Result<VerifyOTPResponse> {
        return userRepository.verifyOTP(email, token)
    }
}