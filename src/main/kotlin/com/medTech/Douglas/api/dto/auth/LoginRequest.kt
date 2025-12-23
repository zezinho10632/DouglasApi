package com.medTech.Douglas.api.dto.auth

data class LoginRequest(
    val email: String,
    val password: String
)
