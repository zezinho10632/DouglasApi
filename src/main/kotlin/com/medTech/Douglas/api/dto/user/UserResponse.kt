package com.medTech.Douglas.api.dto.user

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role
import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: Role,
    val jobTitle: JobTitle?,
    val sectorId: UUID?,
    val active: Boolean,
    val createdAt: LocalDateTime
)
