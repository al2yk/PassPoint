package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCreateRequest
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject
import com.example.passpoint.domain.model.Result

class CreateNewsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(request: NewsCreateRequest): Result<News> =
        repository.createNews(request)
}