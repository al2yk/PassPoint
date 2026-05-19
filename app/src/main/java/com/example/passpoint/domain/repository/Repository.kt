package com.example.passpoint.domain.repository

import com.example.passpoint.data.dto.Certificate
import com.example.passpoint.data.dto.CertificateCreateRequest
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
    suspend fun getCourse(): Result<List<CourseWithEnrollment>>
    suspend fun registerForCourse(courseId: Int, userId: String): Result<CourseRegistration>
    suspend fun unregisterFromCourse(registrationId: Int)
    suspend fun getUserCourseRegistrations(userId: String): Result<List<CourseRegistration>>
    suspend fun createCourse(request: CourseCreateRequest): Result<Course>
    suspend fun updateCourse(courseId: Int, request: CourseCreateRequest): Result<Course>
    suspend fun deleteCourse(courseId: Int): Result<Unit>
    suspend fun createEvent(request: EventCreateRequest): Result<Event>
    suspend fun updateEvent(eventId: Int, request: EventCreateRequest): Result<Event>
    suspend fun deleteEvent(eventId: Int): Result<Unit>
    suspend fun createNews(request: NewsCreateRequest): Result<News>
    suspend fun updateNews(newsId: Int, request: NewsCreateRequest): Result<News>
    suspend fun deleteNews(newsId: Int): Result<Unit>
    suspend fun uploadImage(fileName: String, imageBytes: ByteArray): Result<Unit>
    suspend fun updateUser(userId: String, fields: Map<String, String>): Result<User>
    suspend fun uploadProfileImage(fileName: String, imageBytes: ByteArray): Result<Unit>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun updateUserRole(userId: String, role: Int): Result<User>
    suspend fun getCourseById(courseId: Int): Result<CourseWithEnrollment>
    suspend fun getAttendancesByCourse(courseId: Int): Result<List<CourseRegistration>>
    suspend fun getUsersByIds(ids: List<String>): Result<List<User>>
    suspend fun updateCourseAttendance(attendanceId: Int, newStatus: Int): Result<CourseRegistration>
    suspend fun getAttendancesByCourseIds(ids: List<Int>): Result<List<CourseRegistration>>
    suspend fun getAllAttendances(): Result<List<CourseRegistration>>
    suspend fun getUserCertificates(userId: String): Result<List<Certificate>>
    suspend fun createCertificate(request: CertificateCreateRequest): Result<Certificate>
    suspend fun uploadCertificateFile(fileName: String, fileBytes: ByteArray): Result<Unit>
    suspend fun getCertificatesByCourse(courseId: Int): Result<List<Certificate>>
    suspend fun uploadCourseImage(fileName: String, imageBytes: ByteArray): Result<Unit>
    suspend fun uploadEventImage(fileName: String, imageBytes: ByteArray): Result<Unit>
    suspend fun deleteCertificate(userId: String, courseId: Int): Result<Unit>
    suspend fun deleteCertificateFile(fileName: String): Result<Unit>
}