package com.example.passpoint.presentation.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCategoryUseCase
import com.example.passpoint.domain.useCase.GetNewsUseCase
import com.example.passpoint.presentation.screens.main.news.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNews: GetNewsUseCase,
    private val getCategory: GetCategoryUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(NewsState())
    val state: NewsState get() = _state.value

    private val imageLoader = ImageLoader.Builder(context).build()

    init {
        retry()
    }

    fun retry() {
        getNews()
        getCategory()
    }

    fun selectCategory(categoryId: Int) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
    }

    private fun preloadImages(newsList: List<News>) {
        newsList.forEach { news ->
            if (news.photo.isNotBlank()) {
                val request = ImageRequest.Builder(context)
                    .data(news.photo)
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }

    private fun getNews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                when (val result = getNews.invoke()) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Не удалось загрузить новости"
                        )
                    }
                    is Result.Success<List<News>> -> {
                        val newsData = result.data
                        preloadImages(newsData) // Предзагрузка
                        _state.value = _state.value.copy(
                            news = newsData,
                            isLoading = false,
                            error = if (state.category.isEmpty() && _state.value.error != null) _state.value.error else null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка сети: ${e.message}"
                )
            }
        }
    }

    private fun getCategory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                when (val result = getCategory.invoke()) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Не удалось загрузить категории"
                        )
                    }
                    is Result.Success<List<NewsCategory>> -> {
                        _state.value = _state.value.copy(
                            category = result.data,
                            isLoading = false,
                            error = if (state.news.isEmpty() && _state.value.error != null) _state.value.error else null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка сети: ${e.message}"
                )
            }
        }
    }
}