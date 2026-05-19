package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetUserCertificatesUseCase
import com.example.passpoint.presentation.screens.main.profile.certificates.CertificateSortType
import com.example.passpoint.presentation.screens.main.profile.certificates.CertificatesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertificatesViewModel @Inject constructor(
    private val getUserCertificatesUseCase: GetUserCertificatesUseCase
) : ViewModel() {
    private val _state = mutableStateOf(CertificatesState())
    val state: CertificatesState get() = _state.value

    init { load() }

    fun retry() { load() }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setSortType(type: CertificateSortType) {
        _state.value = _state.value.copy(sortType = type)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _state.value
        val all = currentState.certificates

        // Фильтрация по названию курса
        val filtered = if (currentState.searchQuery.isNotBlank()) {
            all.filter { cert ->
                cert.course_name.contains(currentState.searchQuery, ignoreCase = true)
            }
        } else {
            all
        }

        // Сортировка
        val sorted = when (currentState.sortType) {
            CertificateSortType.NAME_ASC -> filtered.sortedBy { it.course_name.lowercase() }
            CertificateSortType.NAME_DESC -> filtered.sortedByDescending { it.course_name.lowercase() }
            CertificateSortType.DATE_ASC -> filtered.sortedBy { it.created_at ?: "" }
            CertificateSortType.DATE_DESC -> filtered.sortedByDescending { it.created_at ?: "" }
        }

        _state.value = currentState.copy(filteredCertificates = sorted)
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getUserCertificatesUseCase(UserRepository.ID)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        certificates = result.data,
                        isLoading = false
                    )
                    applyFilters()
                }
                is Result.Failure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }
            }
        }
    }
}