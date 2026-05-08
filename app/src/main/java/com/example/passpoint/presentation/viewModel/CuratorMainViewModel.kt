package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.presentation.screens.main.curator.CuratorMainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CuratorMainViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CuratorMainState())
    val state: CuratorMainState get() = _state.value

    init { loadMyCourses() }

    fun retry() { loadMyCourses() }

    private fun loadMyCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getCourseUseCase()) {
                is Result.Success -> {
                    val allCourses = result.data
                    val today = LocalDate.now()
                    val myCourses = allCourses
                        // только курсы, где куратор — текущий пользователь (dbUserId)
                        .filter { it.curator == UserRepository.dbUserId }
                        // оставляем только будущие (включая сегодняшние)
                        .filter {
                            runCatching { LocalDate.parse(it.date) }
                                .getOrDefault(LocalDate.MIN) >= today
                        }
                        // сортируем по дате: сначала ближайшие
                        .sortedBy { LocalDate.parse(it.date) }

                    _state.value = _state.value.copy(
                        myCourses = myCourses,
                        isLoading = false
                    )
                    applyFilters()
                }
                is Result.Failure -> _state.value = _state.value.copy(
                    isLoading = false, error = result.exception.message
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleSort() {
        // Toggle between А-Я and Я-А
        _state.value = _state.value.copy(sortAscending = !_state.value.sortAscending)
        applyFilters()
    }

    private fun applyFilters() {
        val courses = _state.value.myCourses
        var filtered = courses

        val query = _state.value.searchQuery
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.place.contains(query, ignoreCase = true)
            }
        }

        if (_state.value.sortAscending) {
            filtered = filtered.sortedBy { it.name.lowercase() }
        } else {
            filtered = filtered.sortedByDescending { it.name.lowercase() }
        }

        _state.value = _state.value.copy(filteredCourses = filtered)
    }
}