package com.example.passpoint.presentation.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.data.dto.NewsCreateRequest
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.useCase.CreateNewsUseCase
import com.example.passpoint.domain.useCase.GetCategoryUseCase
import com.example.passpoint.domain.useCase.GetNewsUseCase
import com.example.passpoint.domain.useCase.UpdateNewsUseCase
import com.example.passpoint.presentation.screens.main.news.CreateNewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class CreateNewsEvent {
    object Success : CreateNewsEvent()
}
@HiltViewModel
class CreateNewsViewModel @Inject constructor(
    private val createNewsUseCase: CreateNewsUseCase,
    private val updateNewsUseCase: UpdateNewsUseCase,
    private val getNewsUseCase: GetNewsUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val repository: Repository,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newsId: Int? = savedStateHandle.get<Int>("newsId")
    val isEditMode: Boolean get() = newsId != null && newsId > 0

    private val _state = mutableStateOf(CreateNewsState())
    val state: CreateNewsState get() = _state.value

    private val _event = Channel<CreateNewsEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadCategories()
        if (isEditMode) loadNews(newsId!!)
    }

    fun updateTitle(value: String) { _state.value = _state.value.copy(title = value, error = null) }
    fun updateText(value: String) { _state.value = _state.value.copy(text = value, error = null) }
    fun selectCategory(category: NewsCategory) { _state.value = _state.value.copy(selectedCategory = category) }
    fun showDatePicker(show: Boolean) { _state.value = _state.value.copy(showDatePicker = show) }
    fun onDateSelected(date: String) {
        _state.value = _state.value.copy(date = date, showDatePicker = false, error = null)
    }
    fun saveNews() {
        val current = _state.value
        if (current.title.isBlank() || current.text.isBlank() || current.selectedCategory == null || current.date.isBlank()) {
            _state.value = current.copy(error = "Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true, error = null)
            val request = NewsCreateRequest(
                title = current.title,
                new_text = current.text,
                news_category = current.selectedCategory.id,
                create = current.date,
                photo = current.imageUrl ?: ""
            )
            val result = if (isEditMode) {
                updateNewsUseCase(newsId!!, request)
            } else {
                createNewsUseCase(request)
            }
            when (result) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isSending = false)
                    _event.send(CreateNewsEvent.Success)
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(isSending = false, error = result.exception.message)
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCategories = true)
            when (val result = getCategoryUseCase()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(categories = result.data, isLoadingCategories = false)
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(error = result.exception.message, isLoadingCategories = false)
                }
            }
        }
    }

    private fun loadNews(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)
            when (val result = getNewsUseCase()) {
                is Result.Success -> {
                    val news = result.data.find { it.id == id }
                    if (news != null) {
                        val category = _state.value.categories.find { it.id == news.news_category }
                        _state.value = _state.value.copy(
                            title = news.title,
                            text = news.new_text,
                            date = news.create,
                            selectedCategory = category,
                            imageUrl = news.photo,
                            isSending = false
                        )
                    } else {
                        _state.value = _state.value.copy(error = "Новость не найдена", isSending = false)
                    }
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(error = result.exception.message, isSending = false)
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
                    val fileName = "news_${UUID.randomUUID()}.jpg"
                    val result = repository.uploadImage(fileName, byteArray)
                    if (result is Result.Success) {
                        val publicUrl = "https://tzeqggnubimyfappfuab.supabase.co/storage/v1/object/public/NEWS/$fileName"
                        _state.value = _state.value.copy(imageUrl = publicUrl, isUploadingImage = false)
                    } else {
                        _state.value = _state.value.copy(error = (result as Result.Failure).exception.message, isUploadingImage = false)
                    }
                } else {
                    _state.value = _state.value.copy(error = "Не удалось прочитать файл", isUploadingImage = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки: ${e.message}", isUploadingImage = false)
            }
        }
    }
}