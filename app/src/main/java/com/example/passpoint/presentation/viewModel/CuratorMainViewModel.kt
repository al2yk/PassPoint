package com.example.passpoint.presentation.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.worker.ReminderScheduler
import com.example.passpoint.presentation.screens.main.curator.CuratorCourseSortType
import com.example.passpoint.presentation.screens.main.curator.CuratorMainState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CuratorMainViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(CuratorMainState())
    val state: CuratorMainState get() = _state.value

    init { loadMyCourses() }

    fun retry() { loadMyCourses() }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setSortType(type: CuratorCourseSortType) {
        _state.value = _state.value.copy(sortType = type)
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

        val sorted = when (_state.value.sortType) {
            CuratorCourseSortType.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            CuratorCourseSortType.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
            CuratorCourseSortType.DATE_ASC -> filtered.sortedBy { it.date }          // старые сначала
            CuratorCourseSortType.DATE_DESC -> filtered.sortedByDescending { it.date } // новые сначала
        }

        _state.value = _state.value.copy(filteredCourses = sorted)
    }

    private fun loadMyCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getCourseUseCase()) {
                is Result.Success -> {
                    val allCourses = result.data
                    val today = LocalDate.now()
                    val myCourses = allCourses
                        .filter { it.curator == UserRepository.dbUserId && it.curator != null }
                        .filter {
                            runCatching { LocalDate.parse(it.date) }
                                .getOrDefault(LocalDate.MIN) >= today
                        }

                    _state.value = _state.value.copy(
                        myCourses = myCourses,
                        isLoading = false
                    )
                    val todayCourses = myCourses.filter { it.date == LocalDate.now().toString() }
                    if (todayCourses.isNotEmpty()) {
                        todayCourses.forEach {
                            ReminderScheduler.scheduleAttendanceReminder(context, it.date)
                        }
                    }
                    applyFilters()
                }
                is Result.Failure -> _state.value = _state.value.copy(
                    isLoading = false, error = result.exception.message
                )
            }
        }
    }
}