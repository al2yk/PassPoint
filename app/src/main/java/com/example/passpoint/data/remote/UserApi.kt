package com.example.passpoint.data.remote

import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.AuthResponse
import com.example.passpoint.data.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: AuthRequest): AuthResponse
    @POST("/rest/v1/profiles")
    suspend fun createUser(@Body user: User)
    @POST("/auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: AuthRequest): AuthResponse
}