package com.example.passpoint.presentation.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.useCase.GetProfileUseCase
import com.example.passpoint.presentation.screens.main.profile.EditProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class EditProfileEvent {
    object Success : EditProfileEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val repository: Repository,
    private val application: Application
) : ViewModel() {

    private val _state = mutableStateOf(EditProfileState())
    val state: EditProfileState get() = _state.value

    private val _event = Channel<EditProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadProfile()
    }

    fun updateName(value: String) {
        _state.value = _state.value.copy(name = value, error = null)
    }

    fun updateSurname(value: String) {
        _state.value = _state.value.copy(surname = value, error = null)
    }

    fun onPhotoPicked(uri: Uri?) {
        if (uri == null) return
        _state.value = _state.value.copy(newPhotoUri = uri)
    }

    fun removePhoto() {
        _state.value = _state.value.copy(photoUrl = null, newPhotoUri = null)
    }

    fun updatePhone(value: String) {
        val error = if (value.isNotBlank() && !isValidPhone(value)) {
            "Неверный формат (+7 XXX XXX XX XX)"
        } else null
        _state.value = _state.value.copy(phone = value, phoneError = error, error = null)
    }

    fun updateOrganization(value: String) {
        _state.value = _state.value.copy(organization = value, error = null)
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^\\+7\\s\\d{3}\\s\\d{3}\\s\\d{2}\\s\\d{2}$"))
    }
    fun saveProfile() {
        val initial = _state.value
        viewModelScope.launch {
            _state.value = initial.copy(isSaving = true, error = null)
            try {
                // Загружаем новое фото, если выбрано
                if (initial.newPhotoUri != null) {
                    val inputStream =
                        application.contentResolver.openInputStream(initial.newPhotoUri!!)
                    val byteArray = inputStream?.readBytes()
                    inputStream?.close()
                    if (byteArray != null) {
                        val fileName = "user_${UUID.randomUUID()}.jpg"
                        val uploadResult = repository.uploadProfileImage(fileName, byteArray)
                        if (uploadResult is Result.Success) {
                            val publicUrl =
                                "https://tzeqggnubimyfappfuab.supabase.co/storage/v1/object/public/USER_PHOTO/$fileName"
                            _state.value =
                                _state.value.copy(photoUrl = publicUrl, newPhotoUri = null)
                        } else {
                            _state.value = _state.value.copy(
                                isSaving = false,
                                error = (uploadResult as Result.Failure).exception.message
                            )
                            return@launch
                        }
                    }
                }
                val current = _state.value
                if (current.phone.isNotBlank() && !isValidPhone(current.phone)) {
                    _state.value = current.copy(error = "Неверный формат телефона (+7 XXX XXX XX XX)")
                }
                val fields = mutableMapOf<String, String>()
                fields["name"] = current.name
                fields["surname"] = current.surname
                fields["phone"] = current.phone
                fields["organization"] = current.organization
                if (current.photoUrl != null) {
                    fields["photo"] = current.photoUrl
                } else {
                    fields["photo"] = ""
                }
                val updateResult = repository.updateUser(current.userId, fields)
                if (updateResult is Result.Success) {
                    _state.value = _state.value.copy(isSaving = false)
                    _event.send(EditProfileEvent.Success)
                } else {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = (updateResult as Result.Failure).exception.message
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            when (val result = getProfileUseCase.invoke(UserRepository.ID)) {
                is Result.Success -> {
                    val user = result.data.firstOrNull()
                    if (user != null) {
                        _state.value = _state.value.copy(
                            userId = user.id.toString(),
                            name = user.name,
                            surname = user.surname,
                            phone = user.phone ?: "",
                            organization = user.organization ?: "",
                            role = user.role,
                            photoUrl = user.photo,
                            isSaving = false
                        )
                    } else {
                        _state.value =
                            _state.value.copy(error = "Пользователь не найден", isSaving = false)
                    }
                }

                is Result.Failure -> {
                    _state.value =
                        _state.value.copy(error = result.exception.message, isSaving = false)
                }
            }
        }
    }
}