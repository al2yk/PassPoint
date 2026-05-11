package com.example.passpoint.data.dto

data class CertificateCreateRequest(
    val user: String,
    val course_id: Int,
    val course_name: String,
    val user_name: String,
    val certificate_url: String
)