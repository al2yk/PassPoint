package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.*
import com.example.passpoint.presentation.screens.main.ConfirmAction
import com.example.passpoint.presentation.screens.main.ConfirmDialogState
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState
import com.example.passpoint.presentation.screens.main.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getCourseUseCase: GetCourseUseCase,
    private val getCuratorsUseCase: GetCuratorsUseCase,
    private val getUserRegistrationsUseCase: GetUserEventRegistrationsUseCase,
    private val registerForEventUseCase: RegisterForEventUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val registerForCourseUseCase: RegisterForCourseUseCase,
    private val unregisterFromCourseUseCase: UnregisterFromCourseUseCase,
    private val getUserCourseRegistrationsUseCase: GetUserCourseRegistrationsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MainState())
    val state: MainState get() = _state.value

    init {
        loadAllData()
    }

    fun retry() {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val newsDeferred = async { getNewsUseCase() }
            val courseDeferred = async { getCourseUseCase() }
            val eventsDeferred = async { getEventsUseCase() }
            val curatorsDeferred = async { getCuratorsUseCase() }
            val registrationsDeferred = async { getUserRegistrationsUseCase(UserRepository.ID) }
            val courseRegistrationsDeferred = async { getUserCourseRegistrationsUseCase(UserRepository.ID) }

            val newsResult = newsDeferred.await()
            val eventsResult = eventsDeferred.await()
            val curatorsResult = curatorsDeferred.await()
            val courseResult = courseDeferred.await()
            val registrationsResult = registrationsDeferred.await()
            val courseRegsResult = courseRegistrationsDeferred.await()

            var errorMessage: String? = null
            if (newsResult is Result.Failure) errorMessage = "Ошибка загрузки новостей"
            if (courseResult is Result.Failure) errorMessage = "Ошибка загрузки курсов"
            if (eventsResult is Result.Failure) errorMessage = "Ошибка загрузки мероприятий"
            if (curatorsResult is Result.Failure) errorMessage = "Ошибка загрузки кураторов"
            if (registrationsResult is Result.Failure) errorMessage = "Ошибка загрузки регистраций"

            val news = (newsResult as? Result.Success)?.data ?: emptyList()
            val courses = (courseResult as? Result.Success)?.data ?: emptyList()
            val events = (eventsResult as? Result.Success)?.data ?: emptyList()
            val curators = (curatorsResult as? Result.Success)?.data ?: emptyList()
            val registrations = (registrationsResult as? Result.Success)?.data ?: emptyList()
            val courseRegs = (courseRegsResult as? Result.Success)?.data ?: emptyList()

            _state.value = _state.value.copy(
                isLoading = false,
                error = errorMessage,
                news = news,
                events = events,
                curators = curators,
                registrations = registrations,
                course = courses,
                courseRegistrations = courseRegs
            )
        }
    }

    // === Диалог подтверждения ===
    fun showRegisterConfirm(eventId: Int) {
        _state.value = _state.value.copy(
            confirmDialog = ConfirmDialogState(eventId, ConfirmAction.REGISTER)
        )
    }

    fun showUnregisterConfirm(eventId: Int) {
        _state.value = _state.value.copy(
            confirmDialog = ConfirmDialogState(eventId, ConfirmAction.UNREGISTER)
        )
    }

    fun hideDialog() {
        _state.value = _state.value.copy(confirmDialog = null)
    }

    fun confirmAction() {
        val dialog = _state.value.confirmDialog ?: return
        hideDialog()
        when (dialog.action) {
            ConfirmAction.REGISTER -> registerForEvent(dialog.eventId)
            ConfirmAction.UNREGISTER -> unregisterForEvent(dialog.eventId)
            else -> {}
        }
    }

    // === Регистрация/отмена ===
    private fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            when (val result = registerForEventUseCase(eventId, UserRepository.ID)) {
                is Result.Success -> {
                    val newReg = result.data
                    val updated = _state.value.registrations + newReg
                    _state.value = _state.value.copy(
                        registrations = updated,
                        isRegistrationLoading = false
                    )
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        isRegistrationLoading = false,
                        error = "Ошибка регистрации: ${result.exception.message}"
                    )
                }
            }
        }
    }

    private fun unregisterForEvent(eventId: Int) {
        val reg = _state.value.registrations.find { it.event == eventId } ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            try {
                unregisterFromEventUseCase(reg.id)
                val updatedRegistrations = _state.value.registrations.filter { it.event != eventId }
                _state.value = _state.value.copy(
                    registrations = updatedRegistrations,
                    isRegistrationLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRegistrationLoading = false,
                    error = "Ошибка отмены регистрации: ${e.message}"
                )
            }
        }
    }
    fun refreshRegistrations() {
        viewModelScope.launch {
            when (val result = getUserRegistrationsUseCase(UserRepository.ID)) {
                is Result.Success -> _state.value = _state.value.copy(registrations = result.data)
                is Result.Failure -> {  }
            }
            // обновляем регистрации курсов
            when (val result = getUserCourseRegistrationsUseCase(UserRepository.ID)) {
                is Result.Success -> _state.value = _state.value.copy(courseRegistrations = result.data)
                is Result.Failure -> { }
            }
            refreshCourses()
        }
    }

    fun showRegisterCourseConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            courseConfirmDialog = CourseConfirmDialogState(courseId, ConfirmAction.REGISTER)
        )
    }

    fun showUnregisterCourseConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            courseConfirmDialog = CourseConfirmDialogState(courseId, ConfirmAction.UNREGISTER)
        )
    }

    fun hideCourseDialog() {
        _state.value = _state.value.copy(courseConfirmDialog = null)
    }

    fun confirmCourseAction() {
        val dialog = _state.value.courseConfirmDialog ?: return
        hideCourseDialog()
        when (dialog.action) {
            ConfirmAction.REGISTER -> registerForCourse(dialog.courseId)
            ConfirmAction.UNREGISTER -> unregisterFromCourse(dialog.courseId)
            else -> {}
        }
    }
    private fun registerForCourse(courseId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            when (val result = registerForCourseUseCase(courseId, UserRepository.ID)) {
                is Result.Success -> {
                    val newReg = result.data
                    val updated = _state.value.courseRegistrations + newReg
                    _state.value = _state.value.copy(
                        courseRegistrations = updated,
                        isRegistrationLoading = false
                    )
                    refreshCourses()
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        isRegistrationLoading = false,
                        error = "Ошибка записи на курс: ${result.exception.message}"
                    )
                }
            }
        }
    }

    private fun unregisterFromCourse(courseId: Int) {
        val reg = _state.value.courseRegistrations.find { it.course == courseId } ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            try {
                unregisterFromCourseUseCase(reg.id!!)
                val updated = _state.value.courseRegistrations.filter { it.course != courseId }
                _state.value = _state.value.copy(
                    courseRegistrations = updated,
                    isRegistrationLoading = false
                )
                refreshCourses()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRegistrationLoading = false,
                    error = "Ошибка отмены записи на курс: ${e.message}"
                )
            }
        }
    }
    fun refreshCourses() {
        viewModelScope.launch {
            when (val result = getCourseUseCase()) {
                is Result.Success -> _state.value = _state.value.copy(course = result.data)
                is Result.Failure -> { /* тихо игнорируем */ }
            }
        }
    }
}