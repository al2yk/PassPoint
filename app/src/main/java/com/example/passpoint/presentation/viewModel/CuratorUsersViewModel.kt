package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetAttendancesByCourseIdsUseCase
import com.example.passpoint.domain.useCase.GetCourseUseCase
import com.example.passpoint.domain.useCase.GetUsersByIdsUseCase
import com.example.passpoint.presentation.screens.main.curator.CuratorUserInfo
import com.example.passpoint.presentation.screens.main.curator.CuratorUsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CuratorUsersViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase,
    private val getAttendancesByCourseIdsUseCase: GetAttendancesByCourseIdsUseCase,
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CuratorUsersState())
    val state: CuratorUsersState get() = _state.value

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // 1. Все курсы куратора (любые даты)
                when (val coursesResult = getCourseUseCase()) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = coursesResult.exception.message
                        )
                        return@launch
                    }

                    is Result.Success -> {
                        val myCourses =
                            coursesResult.data.filter { it.curator == UserRepository.dbUserId }
                        if (myCourses.isEmpty()) {
                            _state.value = _state.value.copy(isLoading = false, users = emptyList())
                            return@launch
                        }
                        val courseIds = myCourses.map { it.id }

                        // 2. Все записи посещаемости этих курсов
                        when (val attResult = getAttendancesByCourseIdsUseCase(courseIds)) {
                            is Result.Failure -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    error = attResult.exception.message
                                )
                                return@launch
                            }

                            is Result.Success -> {
                                val attendances = attResult.data
                                if (attendances.isEmpty()) {
                                    _state.value =
                                        _state.value.copy(isLoading = false, users = emptyList())
                                    return@launch
                                }

                                val userIds = attendances.map { it.user }.distinct()

                                // 3. Загружаем пользователей
                                when (val usersResult = getUsersByIdsUseCase(userIds)) {
                                    is Result.Failure -> {
                                        _state.value = _state.value.copy(
                                            isLoading = false,
                                            error = usersResult.exception.message
                                        )
                                        return@launch
                                    }

                                    is Result.Success -> {
                                        val users = usersResult.data
                                        // Группируем по пользователю и считаем статусы
                                        val userStats = attendances.groupBy { it.user }
                                            .map { (authId, records) ->
                                                val user =
                                                    users.find { it.user_id.toString() == authId }
                                                if (user != null) {
                                                    val total = records.size
                                                    val attended = records.count { it.status == 2 }
                                                    val missed = records.count { it.status == 3 }
                                                    CuratorUserInfo(user, total, attended, missed)
                                                } else null
                                            }.filterNotNull()

                                        _state.value = _state.value.copy(
                                            users = userStats,
                                            isLoading = false
                                        )
                                        applyFilters()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleSort() {
        _state.value = _state.value.copy(sortAscending = !_state.value.sortAscending)
        applyFilters()
    }

    private fun applyFilters() {
        val allUsers = _state.value.users
        var filtered = allUsers

        val query = _state.value.searchQuery
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.user.name.contains(query, ignoreCase = true) ||
                        it.user.surname.contains(query, ignoreCase = true) ||
                        it.user.email.contains(query, ignoreCase = true)
            }
        }

        filtered = if (_state.value.sortAscending) {
            filtered.sortedBy { "${it.user.name} ${it.user.surname}".lowercase() }
        } else {
            filtered.sortedByDescending { "${it.user.name} ${it.user.surname}".lowercase() }
        }

        _state.value = _state.value.copy(filteredUsers = filtered)
    }
}