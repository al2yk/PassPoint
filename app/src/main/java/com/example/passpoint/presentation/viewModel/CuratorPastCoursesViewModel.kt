package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.presentation.screens.main.curator.PastCoursesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CuratorPastCoursesViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase
) : ViewModel() {
    private val _state = mutableStateOf(PastCoursesState())
    val state: PastCoursesState get() = _state.value

    init { loadPastCourses() }

    fun retry() { loadPastCourses() }

    private fun loadPastCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getCourseUseCase()) {
                is Result.Success -> {
                    val allCourses = result.data
                    val today = LocalDate.now()
                    val pastCourses = allCourses
                        .filter { it.curator == UserRepository.dbUserId && it.curator != null }
                        .filter {
                            runCatching { LocalDate.parse(it.date) }
                                .getOrDefault(LocalDate.MIN) < today
                        }
                        .sortedByDescending { LocalDate.parse(it.date) } // ближайшие прошедшие первыми
                    _state.value = _state.value.copy(
                        courses = pastCourses,
                        isLoading = false
                    )
                }
                is Result.Failure -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.exception.message
                )
            }
        }
    }
}