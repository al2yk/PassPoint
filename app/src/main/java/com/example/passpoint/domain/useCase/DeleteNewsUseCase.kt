package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class DeleteNewsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(newsId: Int): Result<Unit> = repository.deleteNews(newsId)
}