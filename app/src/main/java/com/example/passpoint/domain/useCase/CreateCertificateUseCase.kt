package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Certificate
import com.example.passpoint.data.dto.CertificateCreateRequest
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class CreateCertificateUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(request: CertificateCreateRequest): Result<Certificate> =
        repository.createCertificate(request)
}