package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.DeleteCourseUseCase
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.useCase.GetCuratorsUseCase
import com.example.passpoint.domain.useCase.GetUserCourseRegistrationsUseCase
import com.example.passpoint.domain.useCase.RegisterForCourseUseCase
import com.example.passpoint.domain.useCase.UnregisterFromCourseUseCase
import com.example.passpoint.presentation.screens.main.ConfirmAction
import com.example.passpoint.presentation.screens.main.CourseConfirmDialogState
import com.example.passpoint.presentation.screens.main.course.CourseSortType
import com.example.passpoint.presentation.screens.main.course.CoursesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase,
    private val getUserCourseRegistrationsUseCase: GetUserCourseRegistrationsUseCase,
    private val registerForCourseUseCase: RegisterForCourseUseCase,
    private val unregisterFromCourseUseCase: UnregisterFromCourseUseCase,
    private val deleteCourseUseCase: DeleteCourseUseCase,
    private val getCuratorsUseCase: GetCuratorsUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(CoursesState())
    val state: CoursesState get() = _state.value

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setSortType(type: CourseSortType) {
        _state.value = _state.value.copy(sortType = type)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _state.value
        val upcoming = currentState.upcomingCourses
        val past = currentState.pastCourses

        // Фильтрация
        val filteredUpcoming = if (currentState.searchQuery.isNotBlank()) {
            upcoming.filter { course ->
                course.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        course.description.contains(currentState.searchQuery, ignoreCase = true) ||
                        course.place.contains(currentState.searchQuery, ignoreCase = true)
            }
        } else {
            upcoming
        }

        val filteredPast = if (currentState.searchQuery.isNotBlank()) {
            past.filter { course ->
                course.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        course.description.contains(currentState.searchQuery, ignoreCase = true) ||
                        course.place.contains(currentState.searchQuery, ignoreCase = true)
            }
        } else {
            past
        }

        // Сортировка в зависимости от sortType
        val sortedUpcoming = when (currentState.sortType) {
            CourseSortType.NAME_ASC -> filteredUpcoming.sortedBy { it.name.lowercase() }
            CourseSortType.NAME_DESC -> filteredUpcoming.sortedByDescending { it.name.lowercase() }
            CourseSortType.DATE_ASC -> filteredUpcoming.sortedBy { it.date }          // старые сначала
            CourseSortType.DATE_DESC -> filteredUpcoming.sortedByDescending { it.date } // новые сначала
        }

        val sortedPast = when (currentState.sortType) {
            CourseSortType.NAME_ASC -> filteredPast.sortedBy { it.name.lowercase() }
            CourseSortType.NAME_DESC -> filteredPast.sortedByDescending { it.name.lowercase() }
            CourseSortType.DATE_ASC -> filteredPast.sortedBy { it.date }
            CourseSortType.DATE_DESC -> filteredPast.sortedByDescending { it.date }
        }

        _state.value = currentState.copy(
            filteredUpcomingCourses = sortedUpcoming,
            filteredPastCourses = sortedPast
        )
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val courseResult = getCourseUseCase()
            val regResult = getUserCourseRegistrationsUseCase(UserRepository.ID)
            val curatorResult = getCuratorsUseCase()

            val error = when {
                courseResult is Result.Failure -> "Ошибка загрузки курсов"
                regResult is Result.Failure -> "Ошибка загрузки регистраций"
                curatorResult is Result.Failure -> "Ошибка загрузки кураторов"
                else -> null
            }

            val courses = (courseResult as? Result.Success)?.data ?: emptyList()
            val registrations = (regResult as? Result.Success)?.data ?: emptyList()
            val curators = (curatorResult as? Result.Success)?.data ?: emptyList()
            val today = LocalDate.now()
            val upcoming = courses.filter {
                runCatching { LocalDate.parse(it.date) >= today }.getOrDefault(false)
            }
            val past = courses.filter {
                runCatching { LocalDate.parse(it.date) < today }.getOrDefault(false)
            }

            _state.value = _state.value.copy(
                isLoading = false,
                isRegistrationLoading = false,
                error = error,
                upcomingCourses = upcoming,
                pastCourses = past,
                registrations = registrations,
                curators = curators
            )
            applyFilters()
        }
    }

    fun showUpcoming() {
        _state.value = _state.value.copy(isShowingPast = false)
    }

    fun showPast() {
        _state.value = _state.value.copy(isShowingPast = true)
    }

    fun showRegisterConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            confirmDialog = CourseConfirmDialogState(courseId, ConfirmAction.REGISTER)
        )
    }

    fun showUnregisterConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            confirmDialog = CourseConfirmDialogState(courseId, ConfirmAction.UNREGISTER)
        )
    }

    fun hideDialog() {
        _state.value = _state.value.copy(confirmDialog = null)
    }

    fun confirmAction() {
        val dialog = _state.value.confirmDialog ?: return
        hideDialog()
        when (dialog.action) {
            ConfirmAction.REGISTER -> register(dialog.courseId)
            ConfirmAction.UNREGISTER -> unregister(dialog.courseId)
            else -> {}
        }
    }

    fun showDeleteConfirm(courseId: Int) {
        _state.value = _state.value.copy(
            deleteDialog = CourseConfirmDialogState(courseId, ConfirmAction.DELETE)
        )
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(deleteDialog = null)
    }

    fun confirmDeleteAction() {
        val dialog = _state.value.deleteDialog ?: return
        hideDeleteDialog()
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deleteCourseUseCase(dialog.courseId)) {
                is Result.Success -> loadData()
                is Result.Failure -> _state.value = _state.value.copy(error = result.exception.message, isLoading = false)
            }
        }
    }

    private fun register(courseId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            when (val result = registerForCourseUseCase(courseId, UserRepository.ID)) {
                is Result.Success -> loadData()
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        isRegistrationLoading = false,
                        error = "Ошибка записи: ${result.exception.message}"
                    )
                }
            }
        }
    }

    private fun unregister(courseId: Int) {
        val reg = _state.value.registrations.find { it.course == courseId } ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistrationLoading = true, error = null)
            try {
                unregisterFromCourseUseCase(reg.id!!)
                loadData()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRegistrationLoading = false,
                    error = "Ошибка отмены: ${e.message}"
                )
            }
        }
    }
}