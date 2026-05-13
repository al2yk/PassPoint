package com.example.passpoint.domain.useCase

import com.example.passpoint.domain.repository.Repository
import javax.inject.Inject

class GetAttendancesByCourseIdsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(listInt: List<Int>) = repository.getAttendancesByCourseIds(listInt)
}
