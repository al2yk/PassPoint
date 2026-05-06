package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetCuratorsUseCase @Inject constructor(
    private val userRepository: Repository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getCurators()
    }
}
