package com.example.passpoint.domain.useCase

import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val userRepository: Repository
) {
    suspend operator fun invoke(): Result<List<NewsCategory>> {
        return userRepository.getCategory()
    }
}