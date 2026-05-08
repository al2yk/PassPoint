package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.*
import com.example.passpoint.presentation.screens.main.ConfirmAction
import com.example.passpoint.presentation.screens.main.ConfirmDialogState
import com.example.passpoint.presentation.screens.main.events.EventsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getUserRegistrationsUseCase: GetUserEventRegistrationsUseCase,
    private val registerForEventUseCase: RegisterForEventUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _state = mutableStateOf(EventsState())
    val state: EventsState get() = _state.value

    init {
        loadEventsAndRegistrations()
    }

    fun retry() {
        loadEventsAndRegistrations()
    }

    private fun loadEventsAndRegistrations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val eventsResult = getEventsUseCase()
            val registrationsResult = getUserRegistrationsUseCase(UserRepository.ID)

            val error = when {
                eventsResult is Result.Failure -> "Ошибка загрузки мероприятий"
                registrationsResult is Result.Failure -> "Ошибка загрузки регистраций"
                else -> null
            }

            val events = (eventsResult as? Result.Success)?.data ?: emptyList()
            val registrations = (registrationsResult as? Result.Success)?.data ?: emptyList()
            val today = LocalDate.now()
            val upcoming = events.filter { event ->
                runCatching { LocalDate.parse(event.date) >= today }.getOrDefault(false)
            }
            val past = events.filter { event ->
                runCatching { LocalDate.parse(event.date) < today }.getOrDefault(false)
            }

            _state.value = _state.value.copy(
                isLoading = false,
                error = error,
                upcomingEvents = upcoming,
                pastEvents = past,
                registrations = registrations
            )
        }
    }

    fun showRegisterConfirm(eventId: Int) {
        _state.value = _state.value.copy(confirmDialog = ConfirmDialogState(eventId, ConfirmAction.REGISTER))
    }

    fun showUnregisterConfirm(eventId: Int) {
        _state.value = _state.value.copy(confirmDialog = ConfirmDialogState(eventId, ConfirmAction.UNREGISTER))
    }

    fun hideDialog() {
        _state.value = _state.value.copy(confirmDialog = null)
    }

    fun confirmAction() {
        val dialog = _state.value.confirmDialog ?: return
        hideDialog()
        when (dialog.action) {
            ConfirmAction.REGISTER -> register(dialog.eventId)
            ConfirmAction.UNREGISTER -> unregister(dialog.eventId)
            else -> {}
        }
    }

    fun showUpcoming() {
        _state.value = _state.value.copy(isShowingPast = false)
    }

    fun showPast() {
        _state.value = _state.value.copy(isShowingPast = true)
    }

    private fun register(eventId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            when (val result = registerForEventUseCase(eventId, UserRepository.ID)) {
                is Result.Success -> {
                    val newReg = result.data
                    val updatedRegistrations = _state.value.registrations + newReg
                    _state.value = _state.value.copy(
                        registrations = updatedRegistrations,
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

    private fun unregister(eventId: Int) {
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
    fun showDeleteConfirm(eventId: Int) {
        _state.value = _state.value.copy(
            // нужно добавить deleteDialog в EventsState (см. ниже)
            deleteDialog = ConfirmDialogState(eventId, ConfirmAction.DELETE)
        )
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(deleteDialog = null)
    }

    fun confirmDeleteAction() {
        val dialog = _state.value.deleteDialog ?: return
        hideDeleteDialog()
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deleteEventUseCase(dialog.eventId)) {
                is Result.Success -> loadEventsAndRegistrations()
                is Result.Failure -> _state.value = _state.value.copy(error = result.exception.message, isLoading = false)
            }
        }
    }
}