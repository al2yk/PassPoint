package com.example.passpoint.presentation.screens.main.profile.certificates

import com.example.passpoint.data.dto.Certificate

data class CertificatesState(
    val certificates: List<Certificate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)