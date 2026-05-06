package com.example.passpoint.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passpoint.domain.utils.AndroidNetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoInternetViewModel @Inject constructor(
    private val networkMonitor: AndroidNetworkMonitor
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun retry() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(5000)
            networkMonitor.refresh()
            _isLoading.value = false
        }
    }
}