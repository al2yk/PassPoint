package com.example.passpoint.data.repository

import android.util.Log
import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.Course
import com.example.passpoint.data.dto.CourseCreateRequest
import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.data.dto.EventCreateRequest
import com.example.passpoint.data.dto.EventRegistration
import com.example.passpoint.data.dto.NewPasswordResponse
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.data.dto.NewsCreateRequest
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                photo = null
            )
            Log.d("UserDTO signUp", userDto.toString())

            userApi.createUser(userDto)

            Result.Success(data = authData)
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
            Result.Failure(exception = Exception(e.message))
        }
    }


    override suspend fun signIn(email: String, password: String): Result<AuthResponseModel> {
        return try {
            val response = userApi.signIn(AuthRequest(email, password))
            if (response.access_token == null) {
                return Result.Failure(Exception("Ошибка Авторизации"))
            }
            UserRepository.user_token = response.access_token
            UserRepository.ID = response.user.id.toString()

            val profileResult = getProfile(UserRepository.ID)
            if (profileResult is Result.Success && profileResult.data.isNotEmpty()) {
                UserRepository.role = profileResult.data.first().role
                UserRepository.dbUserId = profileResult.data.first().id.toString()
            }

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
        val response = userApi.deleteEventRegistration("eq.$registrationId")
        if (!response.isSuccessful) {
            throw Exception("Ошибка при удалении регистрации: код ${response.code()}")
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

    override suspend fun getCourse(): Result<List<CourseWithEnrollment>> {
        return try {
            val response = userApi.getCoursesWithEnrollment()
            Result.Success(data = response)
        } catch (e: Exception) {
            Log.e("ERROR getCourse", e.message.toString())
            Result.Failure(exception = e)
        }
    }
    override suspend fun registerForCourse(courseId: Int, userId: String): Result<CourseRegistration> {
        return try {
            val registration = CourseRegistration(
                user = userId,
                course = courseId,
                status = 1
            )
            val response = userApi.createCourseRegistration(registration)
            if (response.isNotEmpty()) {
                Result.Success(response.first())
            } else {
                Result.Failure(Exception("Сервер вернул пустой ответ"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun unregisterFromCourse(registrationId: Int) {
        val response = userApi.deleteCourseRegistration("eq.$registrationId")
        if (!response.isSuccessful) {
            throw Exception("Ошибка при удалении регистрации на курс: код ${response.code()}")
        }
    }

    override suspend fun getUserCourseRegistrations(userId: String): Result<List<CourseRegistration>> {
        return try {
            val response = userApi.getUserCourseRegistrations("eq.$userId")
            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }
    override suspend fun createCourse(request: CourseCreateRequest): Result<Course> {
        return try {
            val response = userApi.createCourse(request)
            if (response.isNotEmpty()) {
                Result.Success(response.first())
            } else {
                Result.Failure(Exception("Сервер вернул пустой ответ"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun updateCourse(courseId: Int, request: CourseCreateRequest): Result<Course> {
        return try {
            val response = userApi.updateCourse("eq.$courseId", request)
            if (response.isNotEmpty()) {
                Result.Success(response.first())
            } else {
                Result.Failure(Exception("Ошибка обновления курса"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteCourse(courseId: Int): Result<Unit> {
        return try {
            val response = userApi.deleteCourse("eq.$courseId")
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Failure(Exception("Ошибка удаления курса: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun createEvent(request: EventCreateRequest): Result<Event> {
        return try {
            val response = userApi.createEvent(request)
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Сервер вернул пустой ответ"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateEvent(eventId: Int, request: EventCreateRequest): Result<Event> {
        return try {
            val response = userApi.updateEvent("eq.$eventId", request)
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Ошибка обновления мероприятия"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: Int): Result<Unit> {
        return try {
            val response = userApi.deleteEvent("eq.$eventId")
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Failure(Exception("Ошибка удаления мероприятия: ${response.code()}"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun createNews(request: NewsCreateRequest): Result<News> {
        return try {
            val response = userApi.createNews(request)
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Сервер вернул пустой ответ"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateNews(newsId: Int, request: NewsCreateRequest): Result<News> {
        return try {
            val response = userApi.updateNews("eq.$newsId", request)
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Ошибка обновления новости"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteNews(newsId: Int): Result<Unit> {
        return try {
            val response = userApi.deleteNews("eq.$newsId")
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Failure(Exception("Ошибка удаления новости: ${response.code()}"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun uploadImage(fileName: String, imageBytes: ByteArray): Result<Unit> {
        return try {
            val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", fileName, requestBody)
            val response = userApi.uploadNewsImage(fileName, imagePart)
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Failure(Exception("Ошибка загрузки изображения: ${response.code()}"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun updateUser(userId: String, fields: Map<String, String>): Result<User> {
        return try {
            val response = userApi.updateUser("eq.$userId", fields)
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Ошибка обновления пользователя"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun uploadProfileImage(fileName: String, imageBytes: ByteArray): Result<Unit> {
        return try {
            val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", fileName, requestBody)
            val response = userApi.uploadProfileImage(fileName, imagePart)
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Failure(Exception("Ошибка загрузки фото профиля: ${response.code()}"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = userApi.getAllUsers()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val response = userApi.deleteUser("eq.$userId")
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Failure(Exception("Ошибка удаления: ${response.code()}"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateUserRole(userId: String, role: Int): Result<User> {
        return try {
            // поле role – int, но в API передаём строку
            val response = userApi.updateUser("eq.$userId", mapOf("role" to role.toString()))
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Ошибка обновления роли"))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    override suspend fun getCourseById(courseId: Int): Result<CourseWithEnrollment> {
        return try {
            val response = userApi.getCourseById("eq.$courseId")
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Курс не найден"))
        } catch (e: Exception) { Result.Failure(e) }
    }

    override suspend fun getAttendancesByCourse(courseId: Int): Result<List<CourseRegistration>> {
        return try {
            val response = userApi.getAttendancesByCourse("eq.$courseId")
            Result.Success(response)
        } catch (e: Exception) { Result.Failure(e) }
    }

    override suspend fun getUsersByIds(ids: List<String>): Result<List<User>> {
        return try {
            val filter = "in.(${ids.joinToString(",")})"
            val response = userApi.getUsersByIds(filter) // теперь фильтр по user_id
            Result.Success(response)
        } catch (e: Exception) { Result.Failure(e) }
    }

    override suspend fun updateCourseAttendance(attendanceId: Int, newStatus: Int): Result<CourseRegistration> {
        return try {
            val response = userApi.updateCourseAttendance(
                "eq.$attendanceId",
                mapOf("status" to newStatus)  // Int ok
            )
            if (response.isNotEmpty()) Result.Success(response.first())
            else Result.Failure(Exception("Ошибка обновления"))
        } catch (e: Exception) { Result.Failure(e) }
    }
    override suspend fun getAttendancesByCourseIds(ids: List<Int>): Result<List<CourseRegistration>> {
        return try {
            val filter = "in.(${ids.joinToString(",")})"
            val response = userApi.getAttendancesByCourses(filter)
            Result.Success(response)
        } catch (e: Exception) { Result.Failure(e) }
    }
    override suspend fun getAllAttendances(): Result<List<CourseRegistration>> {
        return try {
            val response = userApi.getAllAttendances()
            Result.Success(response)
        } catch (e: Exception) { Result.Failure(e) }
    }
}