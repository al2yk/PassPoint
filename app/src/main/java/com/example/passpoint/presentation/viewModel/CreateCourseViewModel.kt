package com.example.passpoint.presentation.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.CourseCreateRequest
import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.useCase.CreateCourseUseCase
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.useCase.GetCuratorsUseCase
import com.example.passpoint.domain.useCase.UpdateCourseUseCase
import com.example.passpoint.presentation.screens.main.course.CreateCourseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class CreateCourseEvent {
    object Success : CreateCourseEvent()
}

@HiltViewModel
class CreateCourseViewModel @Inject constructor(
    private val getCuratorsUseCase: GetCuratorsUseCase,
    private val createCourseUseCase: CreateCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
    private val getCourseUseCase: GetCourseUseCase,
    private val repository: Repository,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: Int? = savedStateHandle.get<String>("courseId")?.toIntOrNull()
    val isEditMode: Boolean get() = courseId != null && courseId > 0

    private val _state = mutableStateOf(CreateCourseState())
    val state: CreateCourseState get() = _state.value

    private val _event = Channel<CreateCourseEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private var pendingCuratorUuid: String? = null

    init {
        if (isEditMode) {
            loadCourse(courseId!!)
        } else {
            loadCurators()
        }
    }

    fun updateName(value: String) {
        _state.value = _state.value.copy(name = value, error = null)
    }

    fun updateDescription(value: String) {
        _state.value = _state.value.copy(description = value, error = null)
    }

    fun updatePlace(value: String) {
        _state.value = _state.value.copy(place = value, error = null)
    }

    fun updateCapacity(value: String) {
        val intValue = value.toIntOrNull()
        _state.value = _state.value.copy(capacity = intValue, error = null)
    }

    fun selectCurator(curator: User) {
        _state.value = _state.value.copy(selectedCurator = curator)
    }

    fun showDatePicker(show: Boolean) {
        _state.value = _state.value.copy(showDatePicker = show)
    }

    fun onDateSelected(date: String) {
        _state.value = _state.value.copy(date = date, showDatePicker = false, error = null)
    }

    fun saveCourse() {
        val current = _state.value
        if (current.name.isBlank() || current.description.isBlank() || current.date.isBlank() ||
            current.place.isBlank() || current.selectedCurator == null || current.capacity == null
        ) {
            _state.value = current.copy(error = "Заполните все поля")
            return
        }
        if (current.capacity <= 0) {
            _state.value = current.copy(error = "Количество мест должно быть больше нуля")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true, error = null)
            val request = CourseCreateRequest(
                name = current.name,
                description = current.description,
                date = current.date,
                place = current.place,
                curator = current.selectedCurator.id.toString(),
                capacity = current.capacity,
                photo = current.imageUrl ?: ""
            )
            val result = if (isEditMode) {
                updateCourseUseCase(courseId!!, request)
            } else {
                createCourseUseCase(request)
            }
            when (result) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isSending = false)
                    _event.send(CreateCourseEvent.Success)
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        isSending = false,
                        error = result.exception.message
                    )
                }
            }
        }
    }

    fun onPhotoPicked(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploadingImage = true, error = null)
            try {
                val inputStream = application.contentResolver.openInputStream(uri)
                val byteArray = inputStream?.readBytes()
                inputStream?.close()
                if (byteArray != null) {
                    val fileName = "course_${UUID.randomUUID()}.jpg"
                    val result = repository.uploadCourseImage(fileName, byteArray)
                    if (result is Result.Success) {
                        val publicUrl = "https://tzeqggnubimyfappfuab.supabase.co/storage/v1/object/public/COURSES/$fileName"
                        _state.value = _state.value.copy(
                            imageUrl = publicUrl,
                            isUploadingImage = false
                        )
                    } else {
                        _state.value = _state.value.copy(
                            error = (result as Result.Failure).exception.message,
                            isUploadingImage = false
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        error = "Не удалось прочитать файл",
                        isUploadingImage = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Ошибка загрузки: ${e.message}",
                    isUploadingImage = false
                )
            }
        }
    }

    private fun loadCurators() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCurators = true)
            when (val result = getCuratorsUseCase()) {
                is Result.Success -> {
                    val curators = result.data
                    val selected = pendingCuratorUuid?.let { uuid ->
                        curators.find { it.id.toString() == uuid }
                    }
                    _state.value = _state.value.copy(
                        curators = curators,
                        selectedCurator = selected ?: _state.value.selectedCurator,
                        isLoadingCurators = false
                    )
                    pendingCuratorUuid = null
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        error = result.exception.message,
                        isLoadingCurators = false
                    )
                }
            }
        }
    }

    private fun loadCourse(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCurators = true)
            when (val result = getCourseUseCase()) {
                is Result.Success -> {
                    val course = result.data.find { it.id == id }
                    if (course != null) {
                        pendingCuratorUuid = course.curator
                        _state.value = _state.value.copy(
                            name = course.name,
                            description = course.description,
                            date = course.date,
                            place = course.place,
                            capacity = course.capacity,
                            imageUrl = course.photo
                        )
                        loadCurators()
                    } else {
                        _state.value = _state.value.copy(
                            error = "Курс не найден",
                            isLoadingCurators = false
                        )
                    }
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        error = result.exception.message,
                        isLoadingCurators = false
                    )
                }
            }
        }
    }
}