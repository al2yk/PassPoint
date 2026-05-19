package com.example.passpoint.presentation.screens.main.profile.certificates

import com.example.passpoint.data.dto.Certificate

enum class CertificateSortType {
    NAME_ASC,   // А-Я
    NAME_DESC,  // Я-А
    DATE_ASC,   // старые сначала
    DATE_DESC   // новые сначала (по умолчанию)
}

data class CertificatesState(
    val certificates: List<Certificate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val sortType: CertificateSortType = CertificateSortType.DATE_DESC,
    val filteredCertificates: List<Certificate> = emptyList()
)