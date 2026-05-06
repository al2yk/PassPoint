package com.example.passpoint.data.remote

import com.example.passpoint.data.dto.AuthRequest
import com.example.passpoint.data.dto.AuthResponse
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
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("/rest/v1/users?select=*")
    suspend fun getProfile(@Query("user_id") userId: String): List<User>
    @GET("/rest/v1/news?select=*")
    suspend fun getNews():  List<News>
    @GET("/rest/v1/newsCategory?select=*")
    suspend fun getCategory():  List<NewsCategory>
    @GET("/rest/v1/users?select=*")
    suspend fun getCurators(@Query("role") role: String): List<User>
    @GET("/rest/v1/events?select=*")
    suspend fun getEvents(): List<Event>
    @Headers("Prefer: return=representation")
    @POST("/rest/v1/event_registrations")
    suspend fun createEventRegistration(@Body registration: EventRegistration): List<EventRegistration>
    @DELETE("/rest/v1/event_registrations")
    suspend fun deleteEventRegistration(@Query("id") idFilter: String): Response<Unit>
    @GET("/rest/v1/event_registrations?select=*")
    suspend fun getUserEventRegistrations(@Query("user") userFilter: String): List<EventRegistration>

    @GET("/rest/v1/course_with_enrollment?select=*")
    suspend fun getCoursesWithEnrollment(): List<CourseWithEnrollment>
    @Headers("Prefer: return=representation")
    @POST("/rest/v1/course_attendance")
    suspend fun createCourseRegistration(@Body registration: CourseRegistration): List<CourseRegistration>

    @DELETE("/rest/v1/course_attendance")
    suspend fun deleteCourseRegistration(@Query("id") idFilter: String): Response<Unit>

    @GET("/rest/v1/course_attendance?select=*")
    suspend fun getUserCourseRegistrations(@Query("user") userFilter: String): List<CourseRegistration>

    @Headers("Prefer: return=representation")
    @POST("/rest/v1/course")
    suspend fun createCourse(@Body course: CourseCreateRequest): List<Course>
    @DELETE("/rest/v1/course")
    suspend fun deleteCourse(@Query("id") idFilter: String): Response<Unit>
    @Headers("Prefer: return=representation")
    @PATCH("/rest/v1/course")
    suspend fun updateCourse(@Query("id") idFilter: String, @Body course: CourseCreateRequest): List<Course>
    @Headers("Prefer: return=representation")
    @POST("/rest/v1/events")
    suspend fun createEvent(@Body event: EventCreateRequest): List<Event>

    @Headers("Prefer: return=representation")
    @PATCH("/rest/v1/events")
    suspend fun updateEvent(@Query("id") idFilter: String, @Body event: EventCreateRequest): List<Event>

    @DELETE("/rest/v1/events")
    suspend fun deleteEvent(@Query("id") idFilter: String): Response<Unit>
    @Headers("Prefer: return=representation")
    @POST("/rest/v1/news")
    suspend fun createNews(@Body news: NewsCreateRequest): List<News>

    @Headers("Prefer: return=representation")
    @PATCH("/rest/v1/news")
    suspend fun updateNews(@Query("id") idFilter: String, @Body news: NewsCreateRequest): List<News>

    @DELETE("/rest/v1/news")
    suspend fun deleteNews(@Query("id") idFilter: String): Response<Unit>
    @Multipart
    @POST("/storage/v1/object/NEWS/{fileName}")
    suspend fun uploadNewsImage(
        @Path("fileName") fileName: String,
        @Part image: MultipartBody.Part
    ): Response<Unit>
}