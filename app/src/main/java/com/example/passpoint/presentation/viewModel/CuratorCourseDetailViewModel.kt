package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetAttendancesByCourseUseCase
import com.example.passpoint.domain.useCase.GetCourseByIdUseCase
import com.example.passpoint.domain.useCase.GetUsersByIdsUseCase
import com.example.passpoint.domain.useCase.UpdateCourseAttendanceUseCase
import com.example.passpoint.presentation.screens.main.curator.AttendanceConfirmDialog
import com.example.passpoint.presentation.screens.main.curator.CuratorCourseDetailState
import com.example.passpoint.presentation.screens.main.curator.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CuratorCourseDetailViewModel @Inject constructor(
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getAttendancesByCourseUseCase: GetAttendancesByCourseUseCase,
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase,
    private val updateCourseAttendanceUseCase: UpdateCourseAttendanceUseCase
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
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
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