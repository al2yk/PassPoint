package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class DeleteCertificateUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(userId: String, courseId: Int): Result<Unit> =
        repository.deleteCertificate(userId, courseId)
}