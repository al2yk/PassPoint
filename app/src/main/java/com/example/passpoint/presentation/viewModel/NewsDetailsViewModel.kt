package com.example.passpoint.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.News
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newsId: Int = savedStateHandle["newsId"] ?: -1

    private val _news = MutableStateFlow<News?>(null)
    val news: StateFlow<News?> = _news

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getNewsUseCase.invoke()) {
                is Result.Success<List<News>> -> {
                    _news.value = result.data.find { it.id == newsId }
                }
                else -> {
                    _news.value = null
                }
            }
            _isLoading.value = false
        }
    }
}