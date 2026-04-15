package com.example.passpoint.domain.model

data class AuthResponseModel (
    val access_token: String,
    val user: AuthUserDomain
)