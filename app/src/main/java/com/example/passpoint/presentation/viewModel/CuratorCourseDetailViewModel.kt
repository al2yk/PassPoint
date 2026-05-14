package com.example.passpoint.presentation.viewModel

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.CertificateCreateRequest
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.CreateCertificateUseCase
import com.example.passpoint.domain.useCase.GetAttendancesByCourseUseCase
import com.example.passpoint.domain.useCase.GetCertificatesByCourseUseCase
import com.example.passpoint.domain.useCase.GetCourseByIdUseCase
import com.example.passpoint.domain.useCase.GetUsersByIdsUseCase
import com.example.passpoint.domain.useCase.UpdateCourseAttendanceUseCase
import com.example.passpoint.domain.useCase.UploadCertificateFileUseCase
import com.example.passpoint.domain.utils.CertificatePdfGenerator
import com.example.passpoint.domain.utils.notification.NotificationChannels.CERTIFICATE
import com.example.passpoint.domain.utils.notification.NotificationHelper
import com.example.passpoint.presentation.MainActivity
import com.example.passpoint.presentation.screens.main.curator.AttendanceConfirmDialog
import com.example.passpoint.presentation.screens.main.curator.CuratorCourseDetailState
import com.example.passpoint.presentation.screens.main.curator.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CuratorCourseDetailViewModel @Inject constructor(
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getAttendancesByCourseUseCase: GetAttendancesByCourseUseCase,
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase,
    private val updateCourseAttendanceUseCase: UpdateCourseAttendanceUseCase,
    private val uploadCertificateFileUseCase: UploadCertificateFileUseCase,
    private val createCertificateUseCase: CreateCertificateUseCase,
    private val getCertificatesByCourseUseCase: GetCertificatesByCourseUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(CuratorCourseDetailState())
    val state = _state

    fun loadData(courseId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // 1. Загружаем курс
                when (val courseResult = getCourseByIdUseCase(courseId)) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = courseResult.exception.message
                        )
                        return@launch
                    }

                    is Result.Success -> _state.value = _state.value.copy(
                        courseName = courseResult.data.name,
                        courseDate = courseResult.data.date
                    )
                }

                // 2. Загружаем посещаемость
                when (val attResult = getAttendancesByCourseUseCase(courseId)) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = attResult.exception.message
                        )
                        return@launch
                    }

                    is Result.Success -> {
                        val attendances = attResult.data
                        if (attendances.isEmpty()) {
                            _state.value =
                                _state.value.copy(participants = emptyList(), isLoading = false)
                            return@launch
                        }

                        val userIds = attendances.map { it.user }.distinct()
                        when (val usersResult = getUsersByIdsUseCase(userIds)) {
                            is Result.Failure -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    error = usersResult.exception.message
                                )
                                return@launch
                            }

                            is Result.Success -> {
                                val users = usersResult.data
                                val participants = attendances.mapNotNull { att ->
                                    users.find { it.user_id.toString() == att.user }?.let { user ->
                                        ParticipantInfo(
                                            user = user,
                                            attendanceId = att.id ?: return@mapNotNull null,
                                            status = att.status
                                        )
                                    }
                                }
                                _state.value = _state.value.copy(
                                    participants = participants,
                                    isLoading = false,
                                    courseId = courseId
                                )
                            }
                        }
                    }
                }
                // 3. Загружаем сертификаты, выданные в этом курсе
                when (val certsResult = getCertificatesByCourseUseCase(courseId)) {
                    is Result.Success -> {
                        val certs = certsResult.data
                        // Обновляем список участников, проставляя certificateIssued = true
                        val updatedParticipants = _state.value.participants.map { participant ->
                            val hasCert = certs.any { it.user == participant.user.user_id.toString() }
                            participant.copy(certificateIssued = hasCert)
                        }
                        _state.value = _state.value.copy(participants = updatedParticipants)
                    }
                    is Result.Failure -> { /* можно залогировать ошибку, но не критично */ }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun issueCertificate(attendanceId: Int) {
        val participant = _state.value.participants.find { it.attendanceId == attendanceId } ?: return
        viewModelScope.launch @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
            // Устанавливаем флаг isIssuing только для этого участника
            val updatedBefore = _state.value.participants.map {
                if (it.attendanceId == attendanceId) it.copy(isIssuing = true) else it
            }
            _state.value = _state.value.copy(participants = updatedBefore)

            val course = _state.value.courseName
            val date = _state.value.courseDate
            val fullName = "${participant.user.name} ${participant.user.surname}"
            val pdfFile = CertificatePdfGenerator.generate(context, fullName, course, date)
            val fileName = "certificates/${UUID.randomUUID()}.pdf"
            val bytes = pdfFile.readBytes()
            when (val uploadResult = uploadCertificateFileUseCase(fileName, bytes)) {
                is Result.Success -> {
                    val publicUrl = "https://tzeqggnubimyfappfuab.supabase.co/storage/v1/object/public/CERTIFICATES/$fileName"
                    val request = CertificateCreateRequest(
                        user = participant.user.user_id.toString(),
                        course_id = _state.value.courseId ?: return@launch,
                        course_name = course,
                        user_name = fullName,
                        certificate_url = publicUrl
                    )
                    when (createCertificateUseCase(request)) {
                        is Result.Success -> {
                            val updatedAfter = _state.value.participants.map {
                                if (it.attendanceId == attendanceId)
                                    it.copy(certificateIssued = true, isIssuing = false)
                                else it
                            }
                            _state.value = _state.value.copy(participants = updatedAfter)
                            val intent = Intent(context, MainActivity::class.java).apply {
                                putExtra("open_certificates", true)
                            }
                            val pending = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                            NotificationHelper.show(context, CERTIFICATE, "Сертификат готов!",
                                "Вы получили сертификат за курс «${course}».", pending)
                        }
                        is Result.Failure -> {
                            val updatedAfter = _state.value.participants.map {
                                if (it.attendanceId == attendanceId) it.copy(isIssuing = false) else it
                            }
                            _state.value = _state.value.copy(
                                participants = updatedAfter,
                                certificateMessage = "Ошибка сохранения сертификата"
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    val updatedAfter = _state.value.participants.map {
                        if (it.attendanceId == attendanceId) it.copy(isIssuing = false) else it
                    }
                    _state.value = _state.value.copy(
                        participants = updatedAfter,
                        certificateMessage = "Ошибка загрузки файла"
                    )
                }
            }
        }
    }
    fun showConfirm(attendanceId: Int, userName: String, newStatus: Int) {
        _state.value = _state.value.copy(
            confirmDialog = AttendanceConfirmDialog(
                attendanceId,
                userName,
                _state.value.courseName,
                newStatus
            )
        )
    }

    fun hideConfirm() {
        _state.value = _state.value.copy(confirmDialog = null)
    }

    fun confirmAttendance() {
        val dialog = _state.value.confirmDialog ?: return
        hideConfirm()
        viewModelScope.launch {
            when (val result =
                updateCourseAttendanceUseCase(dialog.attendanceId, dialog.newStatus)) {
                is Result.Success -> {
                    val updated = _state.value.participants.map {
                        if (it.attendanceId == dialog.attendanceId) it.copy(status = dialog.newStatus) else it
                    }
                    _state.value = _state.value.copy(participants = updated)
                }

                is Result.Failure -> _state.value =
                    _state.value.copy(error = result.exception.message)
            }
        }
    }
}