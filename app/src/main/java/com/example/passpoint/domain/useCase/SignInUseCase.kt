package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result


class SignInUseCase @Inject constructor
    (private val userRepository: Repository) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<AuthResponseModel> {
        return userRepository.signIn(email, password)
    }
}