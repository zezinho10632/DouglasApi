package com.medTech.Douglas.api.dto.auth

import java.util.UUID

data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val userId: UUID,
    val name: String,
    val email: String,
    val role: String,
    val jobTitle: String?
)
