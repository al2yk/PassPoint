package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.data.dto.User
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.DeleteUserUseCase
import com.example.passpoint.domain.useCase.GetAllUsersUseCase
import com.example.passpoint.domain.useCase.UpdateUserRoleUseCase
import com.example.passpoint.presentation.screens.main.admin.UsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val updateUserRoleUseCase: UpdateUserRoleUseCase
) : ViewModel() {

    private val _state = mutableStateOf(UsersState())
    val state: UsersState get() = _state.value

    init {
        loadUsers()
    }

    fun retry() {
        loadUsers()
    }

    private fun calculateWeeklyRegistrations(users: List<User>): List<Int> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val weeks = mutableListOf<Int>()
        for (i in 4 downTo 0) {
            val weekStart = startOfWeek.minusWeeks(i.toLong())
            val weekEnd = weekStart.plusDays(6)
            val count = users.count { user ->
                user.created_at?.let {
                    try {
                        val date = LocalDate.parse(it.substringBefore("T"))
                        date in weekStart..weekEnd
                    } catch (e: Exception) { false }
                } ?: false
            }
            weeks.add(count)
        }
        return weeks
    }

    private fun calculateMonthlyRegistrations(users: List<User>): List<Int> {
        val today = LocalDate.now()
        val months = (4 downTo 0).map { i ->
            val target = today.minusMonths(i.toLong())
            val monthStart = LocalDate.of(target.year, target.month, 1)
            val monthEnd = monthStart.plusMonths(1).minusDays(1)
            users.count { user ->
                user.created_at?.let {
                    try {
                        val date = LocalDate.parse(it.substringBefore("T"))
                        date in monthStart..monthEnd
                    } catch (e: Exception) { false }
                } ?: false
            }
        }
        return months
    }
    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getAllUsersUseCase()) {
                is Result.Success -> {
                    val all = result.data
                    _state.value = _state.value.copy(
                        users = all,
                        weeklyRegistrations = calculateWeeklyRegistrations(all),
                        monthlyRegistrations = calculateMonthlyRegistrations(all),
                        isLoading = false,
                        statsTotal = all.size,
                        statsByRole = all.groupBy { it.role }.mapValues { it.value.size },
                        statsNewThisWeek = calculateNewThisWeek(all)
                    )
                    applyFilters()
                }

                is Result.Failure -> _state.value =
                    _state.value.copy(isLoading = false, error = result.exception.message)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setRoleFilter(role: Int?) {
        _state.value = _state.value.copy(selectedRoleFilter = role)
        applyFilters()
    }

    fun toggleSort() {
        _state.value = _state.value.copy(sortAlphabetically = !_state.value.sortAlphabetically)
        applyFilters()
    }

    private fun applyFilters() {
        val all = _state.value.users
        var filtered = all

        val query = _state.value.searchQuery
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.surname.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
        }

        _state.value.selectedRoleFilter?.let { role ->
            filtered = filtered.filter { it.role == role }
        }

        if (_state.value.sortAlphabetically) {
            filtered = filtered.sortedBy { it.name.lowercase() }
        } else {
            // сортировка по дате создания (сначала новые)
            filtered = filtered.sortedByDescending { it.created_at }
        }

        _state.value = _state.value.copy(filteredUsers = filtered)
    }

    private fun calculateNewThisWeek(users: List<User>): Int {
        val now = java.time.LocalDate.now()
        val weekAgo = now.minusDays(7)
        return users.count { user ->
            user.created_at?.let { createdAt ->
                try {
                    val date = java.time.LocalDate.parse(createdAt.substringBefore("T"))
                    date >= weekAgo
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }
    }

    // Диалог удаления
    fun showDeleteDialog(userId: String) {
        _state.value = _state.value.copy(deleteDialogUserId = userId)
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(deleteDialogUserId = null)
    }

    fun confirmDeleteUser() {
        val id = _state.value.deleteDialogUserId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (deleteUserUseCase(id)) {
                is Result.Success -> loadUsers()
                is Result.Failure -> _state.value =
                    _state.value.copy(isLoading = false, error = "Ошибка удаления")
            }
            _state.value = _state.value.copy(deleteDialogUserId = null)
        }
    }

    // Диалог роли
    fun showRoleDialog(userId: String) {
        _state.value = _state.value.copy(roleDialogUserId = userId)
    }

    fun hideRoleDialog() {
        _state.value = _state.value.copy(roleDialogUserId = null)
    }

    fun selectRoleForDialog(role: Int) {
        _state.value = _state.value.copy(selectedRoleForDialog = role)
    }

    fun confirmRoleChange() {
        val userId = _state.value.roleDialogUserId ?: return
        val role = _state.value.selectedRoleForDialog ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (updateUserRoleUseCase(userId, role)) {
                is Result.Success -> {
                    loadUsers()
                    _state.value =
                        _state.value.copy(roleDialogUserId = null, selectedRoleForDialog = null)
                }

                is Result.Failure -> _state.value =
                    _state.value.copy(isLoading = false, error = "Ошибка смены роли")
            }
        }
    }
}