package com.example.passpoint.presentation.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.passpoint.data.dto.News
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.DeleteNewsUseCase
import com.example.passpoint.domain.useCase.GetCategoryUseCase
import com.example.passpoint.domain.useCase.GetNewsUseCase
import com.example.passpoint.presentation.screens.main.news.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val deleteNewsUseCase: DeleteNewsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(NewsState())
    val state: NewsState get() = _state.value

    private val imageLoader = ImageLoader.Builder(context).build()

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val newsResult = getNewsUseCase()
            val categoryResult = getCategoryUseCase()

            val error = when {
                newsResult is Result.Failure -> "Ошибка загрузки новостей: ${newsResult.exception.message}"
                categoryResult is Result.Failure -> "Ошибка загрузки категорий: ${categoryResult.exception.message}"
                else -> null
            }

            val news = (newsResult as? Result.Success)?.data ?: emptyList()
            val categories = (categoryResult as? Result.Success)?.data ?: emptyList()

            _state.value = _state.value.copy(
                isLoading = false,
                error = error,
                news = news,
                category = categories
            )
            if (news.isNotEmpty()) preloadImages(news)
        }
    }

    fun selectCategory(categoryId: Int) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
    }

    private fun preloadImages(newsList: List<News>) {
        newsList.forEach { news ->
            if (news.photo != null && news.photo.isNotBlank()) {
                val request = ImageRequest.Builder(context)
                    .data(news.photo)
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }

    fun showDeleteConfirm(newsId: Int) {
        _state.value = _state.value.copy(deleteDialog = newsId)
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(deleteDialog = null)
    }

    fun confirmDeleteAction() {
        val newsId = _state.value.deleteDialog ?: return
        hideDeleteDialog()
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deleteNewsUseCase(newsId)) {
                is Result.Success -> {
                    loadData()   // перезагрузка списка
                }

                is Result.Failure -> {
                    _state.value =
                        _state.value.copy(error = result.exception.message, isLoading = false)
                }
            }
        }
    }
}