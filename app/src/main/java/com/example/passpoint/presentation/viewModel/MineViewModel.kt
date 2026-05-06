package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.useCase.GetEventsUseCase
import com.example.passpoint.domain.useCase.GetUserCourseRegistrationsUseCase
import com.example.passpoint.domain.useCase.GetUserEventRegistrationsUseCase
import com.example.passpoint.domain.useCase.UnregisterFromCourseUseCase
import com.example.passpoint.domain.useCase.UnregisterFromEventUseCase
import com.example.passpoint.presentation.screens.main.ConfirmAction
import com.example.passpoint.presentation.screens.main.ConfirmDialogState
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState
import com.example.passpoint.presentation.screens.main.mine.MineState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getCourseUseCase: GetCourseUseCase,
    private val getUserRegistrationsUseCase: GetUserEventRegistrationsUseCase,
    private val getUserCourseRegistrationsUseCase: GetUserCourseRegistrationsUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val unregisterFromCourseUseCase: UnregisterFromCourseUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MineState())
    val state: MineState get() = _state.value

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

     fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val eventsDeferred = async { getEventsUseCase() }
            val coursesDeferred = async { getCourseUseCase() }
            val eventRegsDeferred = async { getUserRegistrationsUseCase(UserRepository.ID) }
            val courseRegsDeferred = async { getUserCourseRegistrationsUseCase(UserRepository.ID) }

            val eventsResult = eventsDeferred.await()
            val coursesResult = coursesDeferred.await()
            val eventRegsResult = eventRegsDeferred.await()
            val courseRegsResult = courseRegsDeferred.await()

            if (eventsResult is Result.Failure || coursesResult is Result.Failure ||
                eventRegsResult is Result.Failure || courseRegsResult is Result.Failure
            ) {
                _state.value = _state.value.copy(isLoading = false, error = "Ошибка загрузки данных")
                return@launch
            }

            val allEvents = (eventsResult as Result.Success).data
            val allCourses = (coursesResult as Result.Success).data
            val eventRegs = (eventRegsResult as Result.Success).data
            val courseRegs = (courseRegsResult as Result.Success).data

            val registeredEventIds = eventRegs.map { it.event }.toSet()
            val registeredCourseIds = courseRegs.map { it.course }.toSet()

            val registeredEvents = allEvents.filter { it.id in registeredEventIds }
            val registeredCourses = allCourses.filter { it.id in registeredCourseIds }

            _state.value = _state.value.copy(
                isLoading = false,
                registeredEvents = registeredEvents,
                registeredCourses = registeredCourses
            )
        }
    }

    fun showEventUnregisterConfirm(eventId: Int) {
        _state.value = _state.value.copy(
            eventConfirmDialog = ConfirmDialogState(eventId, ConfirmAction.UNREGISTER)
        )
    }

    fun showCourseUnregisterConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            courseConfirmDialog = CourseConfirmDialogState(courseId, ConfirmAction.UNREGISTER)
        )
    }

    fun hideEventDialog() {
        _state.value = _state.value.copy(eventConfirmDialog = null)
    }

    fun hideCourseDialog() {
        _state.value = _state.value.copy(courseConfirmDialog = null)
    }

    // Подтверждение отмены
    fun confirmEventAction() {
        val dialog = _state.value.eventConfirmDialog ?: return
        hideEventDialog()
        unregisterEvent(dialog.eventId)
    }

    fun confirmCourseAction() {
        val dialog = _state.value.courseConfirmDialog ?: return
        hideCourseDialog()
        unregisterCourse(dialog.courseId)
    }

    private fun unregisterEvent(eventId: Int) {
        val reg = _state.value.registeredEvents.find { it.id == eventId } ?: return
        // нам нужен registrationId, но EventRegistration хранит id записи.
        // Нужно загрузить регистрации и найти id. Загрузим повторно или хранить в состоянии регистрации.
        // Проще: загрузим регистрации событий, найдём нужный id и вызовем unregister.
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true)
            when (val result = getUserRegistrationsUseCase(UserRepository.ID)) {
                is Result.Success -> {
                    val eventReg = result.data.find { it.event == eventId }
                    if (eventReg != null) {
                        try {
                            unregisterFromEventUseCase(eventReg.id)
                            _state.value = _state.value.copy(
                                registeredEvents = _state.value.registeredEvents.filter { it.id != eventId },
                                isRegistrationLoading = false
                            )
                        } catch (e: Exception) {
                            _state.value = _state.value.copy(isRegistrationLoading = false, error = e.message)
                        }
                    }
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(isRegistrationLoading = false, error = "Ошибка")
                }
            }
        }
    }

    private fun unregisterCourse(courseId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true)
            when (val result = getUserCourseRegistrationsUseCase(UserRepository.ID)) {
                is Result.Success -> {
                    val courseReg = result.data.find { it.course == courseId }
                    if (courseReg != null) {
                        try {
                            unregisterFromCourseUseCase(courseReg.id!!)
                            _state.value = _state.value.copy(
                                registeredCourses = _state.value.registeredCourses.filter { it.id != courseId },
                                isRegistrationLoading = false
                            )
                        } catch (e: Exception) {
                            _state.value = _state.value.copy(isRegistrationLoading = false, error = e.message)
                        }
                    }
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(isRegistrationLoading = false, error = "Ошибка")
                }
            }
        }
    }
}