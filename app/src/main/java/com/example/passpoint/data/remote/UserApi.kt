package com.example.passpoint.data.remote

import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.AuthResponse
import com.example.passpoint.data.dto.NewPasswordResponse
import com.example.passpoint.data.dto.OTPRequest
import com.example.passpoint.data.dto.User
import com.example.passpoint.data.dto.VerifyOTPResponse
import com.example.passpoint.data.dto.VerifyOTPdto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi {

    @POST("/auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: AuthRequest): AuthResponse
    @POST("/rest/v1/users")
    suspend fun createUser(@Body user: User)
    @POST("/auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: AuthRequest): AuthResponse
    @POST("/auth/v1/recover")
    suspend fun sendOTP(@Body email: OTPRequest)
    @POST("/auth/v1/verify?")
    suspend fun verifyOTP(@Body verifyOTP: VerifyOTPdto): VerifyOTPResponse
    @PUT("/auth/v1/user?")
    suspend fun newPassword(@Body authRequest: AuthRequest): NewPasswordResponse
}