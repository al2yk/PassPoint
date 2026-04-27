package com.example.passpoint.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.News
import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCuratorsUseCase
import com.example.passpoint.domain.useCase.GetNewsUseCase
import com.example.passpoint.presentation.screens.main.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getNews: GetNewsUseCase,
    private val getCurators: GetCuratorsUseCase
) : ViewModel() {
    private val _state = mutableStateOf(MainState())
    val state: MainState get() = _state.value

    fun updateState(newState: MainState) {
        _state.value = newState
    }

    init {
       retry()
    }

    fun getNews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                when (val result = getNews.invoke()) {
                    is Result.Failure -> {
                        Log.e("fail", result.toString())
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Не удалось загрузить данные"
                        )
                    }
                    is Result.Success<List<News>> -> {
                        _state.value = _state.value.copy(
                            news = result.data,
                            isLoading = false,
                            error = null
                        )
                        Log.e("Огонь", "получены новости")
                        Log.e("Огонь", state.news.toString())
                    }
                }
            } catch (e: Exception) {
                Log.d("не получены новости", e.toString())
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка сети: ${e.message}"
                )
            }
        }
    }

    fun getCurators() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                when (val result = getCurators.invoke()) {
                    is Result.Failure -> {
                        Log.e("fail", result.toString())
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Не удалось загрузить данные"
                        )
                    }
                    is Result.Success<List<User>> -> {
                        _state.value = _state.value.copy(
                            curators = result.data,
                            isLoading = false,
                            error = null
                        )
                        Log.e("Огонь", "получены кураторы")
                        Log.e("Огонь", state.news.toString())
                    }
                }
            } catch (e: Exception) {
                Log.d("не получены кураторы", e.toString())
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка сети: ${e.message}"
                )
            }
        }
    }

    fun retry() {
        getNews()
        getCurators()
    }
}