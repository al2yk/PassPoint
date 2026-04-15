package com.example.passpoint.domain.repository

import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.model.Result

interface Repository {
    suspend fun signUp(name: String, email: String, password: String,surname: String): Result<AuthResponseModel>
    suspend fun signIn(email: String, password: String): Result<AuthResponseModel>
}