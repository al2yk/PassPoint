package com.example.passpoint.data.mapper

import com.example.passpoint.data.dto.AuthResponse
import com.example.passpoint.data.dto.AuthUserDto
import com.example.passpoint.domain.model.AuthResponseModel
import com.example.passpoint.domain.model.AuthUserDomain

object Mapper {
    fun mapToDomain(response: AuthResponse): AuthResponseModel {
        return AuthResponseModel(
            access_token = response.access_token,
            user = mapToDomain(response.user)
        )
    }

    fun mapToDomain(dto: AuthUserDto): AuthUserDomain {
        return AuthUserDomain(
            id = dto.id,
            email = dto.email
        )
    }
}