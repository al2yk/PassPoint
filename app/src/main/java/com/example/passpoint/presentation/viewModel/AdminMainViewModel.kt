package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.useCase.GetEventsUseCase
import com.example.passpoint.presentation.screens.main.admin.AdminMainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AdminMainState())
    val state: AdminMainState get() = _state.value

    init {
        loadSummary()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            val coursesResult = getCourseUseCase()
            val eventsResult = getEventsUseCase()

            val courses = (coursesResult as? Result.Success)?.data ?: emptyList()
            val events = (eventsResult as? Result.Success)?.data ?: emptyList()

            val today = LocalDate.now()
            val activeCourses = courses.filter { course ->
                runCatching { LocalDate.parse(course.date) >= today }.getOrDefault(false)
            }.size.toString()
            val todayCourses = courses.count { course ->
                runCatching { LocalDate.parse(course.date) == today }.getOrDefault(false)
            }.toString()

            val upcomingEvents = events.filter { event ->
                runCatching { LocalDate.parse(event.date) >= today }.getOrDefault(false)
            }.size.toString()
            val todayEvents = events.count { event ->
                runCatching { LocalDate.parse(event.date) == today }.getOrDefault(false)
            }.toString()

            _state.value = AdminMainState(
                totalCourses = courses.size.toString(),
                activeCourses = activeCourses,
                todayCourses = todayCourses,
                totalEvents = events.size.toString(),
                upcomingEvents = upcomingEvents,
                todayEvents = todayEvents
            )
        }
    }
}