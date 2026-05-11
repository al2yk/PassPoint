package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.Certificate
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class GetCertificatesByCourseUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(courseId: Int): Result<List<Certificate>> =
        repository.getCertificatesByCourse(courseId)
}