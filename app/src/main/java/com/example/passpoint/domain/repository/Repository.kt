package com.example.passpoint.domain.repository

import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.data.dto.NewPasswordResponse
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.data.dto.User
import com.example.passpoint.data.dto.VerifyOTPResponse
import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.model.Result

interface Repository {
    suspend fun signUp(name: String, email: String, password: String,surname: String): Result<AuthResponseModel>
    suspend fun signIn(email: String, password: String): Result<AuthResponseModel>
    suspend fun sendOTP(email: String)
    suspend fun verifyOTP(email: String, token: String): Result<VerifyOTPResponse>
    suspend fun newPassword(email: String, password: String): Result<NewPasswordResponse>
    suspend fun getProfile(userId: String): Result<List<User>>
    suspend fun getNews(): Result<List<News>>
    suspend fun getCategory(): Result<List<NewsCategory>>
    suspend fun getCurators(): Result<List<User>>
    suspend fun getEvent(): Result<List<Event>>
    suspend fun registerForEvent(eventId: Int, userId: String): Result<EventRegistration>
    suspend fun unregisterFromEvent(registrationId: Long?)
    suspend fun getUserRegistrations(userId: String): Result<List<EventRegistration>>
}