package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.EventCreateRequest
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.CreateEventUseCase
import com.example.passpoint.domain.useCase.GetEventsUseCase
import com.example.passpoint.domain.useCase.UpdateEventUseCase
import com.example.passpoint.presentation.screens.main.events.CreateEventState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateEventEvent {
    object Success : CreateEventEvent()
}

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: Int? = savedStateHandle.get<Int>("eventId")
    val isEditMode: Boolean get() = eventId != null && eventId > 0

    private val _state = mutableStateOf(CreateEventState())
    val state: CreateEventState get() = _state.value

    private val _event = Channel<CreateEventEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        if (isEditMode) loadEvent(eventId!!)
    }

    fun updateName(value: String) { _state.value = _state.value.copy(name = value, error = null) }
    fun updatePlace(value: String) { _state.value = _state.value.copy(place = value, error = null) }
    fun showDatePicker(show: Boolean) { _state.value = _state.value.copy(showDatePicker = show) }
    fun onDateSelected(date: String) {
        _state.value = _state.value.copy(date = date, showDatePicker = false, error = null)
    }

    fun saveEvent() {
        val current = _state.value
        if (current.name.isBlank() || current.date.isBlank() || current.place.isBlank()) {
            _state.value = current.copy(error = "Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true, error = null)
            val request = EventCreateRequest(
                name = current.name,
                date = current.date,
                place = current.place
            )
            val result = if (isEditMode) {
                updateEventUseCase(eventId!!, request)
            } else {
                createEventUseCase(request)
            }
            when (result) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isSending = false)
                    _event.send(CreateEventEvent.Success)
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(isSending = false, error = result.exception.message)
                }
            }
        }
    }

    private fun loadEvent(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true) // показываем загрузку
            when (val result = getEventsUseCase()) {
                is Result.Success -> {
                    val event = result.data.find { it.id == id }
                    if (event != null) {
                        _state.value = _state.value.copy(
                            name = event.name,
                            date = event.date,
                            place = event.place,
                            isSending = false
                        )
                    } else {
                        _state.value = _state.value.copy(error = "Мероприятие не найдено", isSending = false)
                    }
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(error = result.exception.message, isSending = false)
                }
            }
        }
    }
}