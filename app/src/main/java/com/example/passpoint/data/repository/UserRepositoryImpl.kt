package com.example.passpoint.data.repository

import android.util.Log
import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.data.dto.NewPasswordResponse
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.data.dto.OTPRequest
import com.example.passpoint.data.dto.User
import com.example.passpoint.data.dto.VerifyOTPResponse
import com.example.passpoint.data.dto.VerifyOTPdto
import com.example.passpoint.data.mapper.Mapper
import com.example.passpoint.data.remote.UserApi
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : Repository {
    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        surname: String
    ): Result<AuthResponseModel> {
        return try {
            val response = userApi.signUp(AuthRequest(email, password))
            Log.d("Response signUp", response.toString())

            UserRepository.user_token = response.access_token
            UserRepository.email = email

            val authData = Mapper.mapToDomain(response)
            Log.d("AuthData signUp", authData.toString())

            val userDto = User(
                id = UUID.randomUUID(),
                email = email,
                user_id = response.user.id,
                name = name,
                surname = surname,
                role = 1,
                photo = ""
            )
            Log.d("UserDTO signUp", userDto.toString())

            userApi.createUser(userDto)

            Result.Success(data = authData)
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }


    override suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthResponseModel> {
        return try {
            val response = userApi.signIn(AuthRequest(email, password))
            Log.d("Response signIn", response.toString())

            if (response.access_token == null) {
                return Result.Failure(exception = Exception("Ошибка Авторизации"))
            }
            UserRepository.user_token = response.access_token
            val authData = Mapper.mapToDomain(response)
            Result.Success(data = authData)

        } catch (e: Exception) {
            Log.e("ERROR SignIn", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun sendOTP(email: String) {
        try {
            val response = userApi.sendOTP(OTPRequest(email = email))
            Log.d("Response sendOTP", response.toString())

        } catch (e: Exception) {
            Log.e("ERROR sendOTP", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun verifyOTP(
        email: String,
        token: String
    ): Result<VerifyOTPResponse> {
        return try {
            val response = userApi.verifyOTP(VerifyOTPdto("email", email, token))
            Log.d("Response verifyOTP", response.toString())
            UserRepository.user_token = response.access_token
            UserRepository.user_token?.let { Log.d("UserRepository.user_token", it) }
            Result.Success(data = response)

        } catch (e: Exception) {
            Log.e("ERROR verifyOTP", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun newPassword(
        email: String,
        password: String
    ): Result<NewPasswordResponse> {
        return try {
            val response = userApi.newPassword(AuthRequest(email = email, password = password))
            Log.d("Response newPassword", response.toString())
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR newPassword", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun getProfile(userId: String): Result<List<User>> {
        return try {
            val response = userApi.getProfile("eq.$userId")
            Log.d("Response getProfile", response.toString())
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getProfile", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun getNews(): Result<List<News>> {
        return try {
            val response = userApi.getNews()
            Log.d("Response getNews", response.toString())
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getNews", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun getCategory(): Result<List<NewsCategory>> {
        return try {
            val response = userApi.getCategory()
            Log.d("Response getCategory", response.toString())
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getCategory", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun getCurators(): Result<List<User>> {
        return try {
            val response = userApi.getCurators("eq.2")
            Log.d("Response getCurators", response.toString())
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getCurators", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }

    override suspend fun getEvent(): Result<List<Event>> {
        return try {
            val response = userApi.getEvents()
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getEvents", e.message.toString())
            Result.Failure(exception = e)
        }
    }


    override suspend fun registerForEvent(eventId: Int, userId: String): Result<EventRegistration> {
        return try {

            val registration = EventRegistration(user = userId, event = eventId)
            val response = userApi.createEventRegistration(registration)
            if (response.isNotEmpty()) {
                Result.Success(response.first())
            } else {
                Result.Failure(Exception("Сервер вернул пустой ответ"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun unregisterFromEvent(registrationId: Long?) {
        try {
            userApi.deleteEventRegistration("eq.$registrationId")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserRegistrations(userId: String): Result<List<EventRegistration>> {
        return try {
            val response = userApi.getUserEventRegistrations("eq.$userId")
            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }
}