package com.example.passpoint.data.repository

import android.util.Log
import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.User
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
        surname: String,
        email: String,
        password: String
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
                name = name,
                surname = surname,
                role = "",
                user_id = response.user.id
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
}