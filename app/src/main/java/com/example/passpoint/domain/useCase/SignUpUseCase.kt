package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.model.Result
import javax.inject.Inject

class SignUpUseCase @Inject constructor
    (private val userRepository: Repository){
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        surname: String
    ): Result<AuthResponseModel> {
        return userRepository.signUp(name, email, password, surname)
    }
}