package com.example.passpoint.presentation.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.GetUserCertificatesUseCase
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

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getUserCertificatesUseCase(UserRepository.ID)) {
                is Result.Success -> _state.value = _state.value.copy(certificates = result.data, isLoading = false)
                is Result.Failure -> _state.value = _state.value.copy(isLoading = false, error = result.exception.message)
            }
        }
    }
}