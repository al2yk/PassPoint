package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor
    (private val userRepository: Repository) {
    suspend operator fun invoke(email: String) {
        return userRepository.sendOTP(email)
    }
}
