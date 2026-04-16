package com.example.passpoint.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.News
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetNewsUseCase
import com.example.passpoint.presentation.screens.main.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getNews: GetNewsUseCase
) : ViewModel() {
    private val _state = mutableStateOf(MainState())
    val state: MainState get() = _state.value

    fun updatestate(newstate: MainState) {
        _state.value = newstate
    }
    init {
        getNews()
    }
    fun getNews() {
        viewModelScope.launch {
            try {
                when (val result = getNews.invoke()) {
                    is Result.Failure -> {
                        Log.e("fail", result.toString())
                    }

                    is Result.Success<List<News>> -> {
                        _state.value = _state.value.copy(
                            news = result.data
                        )
                        Log.e("Огонь", "получены новости ")
                        Log.e("Огонь", state.news.toString())
                    }
                }
            } catch (e: Exception) {
                Log.d("не получены новости", e.toString())
            }
        }
    }
}