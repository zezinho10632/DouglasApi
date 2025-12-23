package com.medTech.Douglas.api.dto.auth

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role

data class RegisterUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: Role = Role.MANAGER,
    val jobTitle: JobTitle? = null,
    val sectorId: String? = null
)
