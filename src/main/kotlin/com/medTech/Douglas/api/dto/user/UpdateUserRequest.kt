package com.medTech.Douglas.api.dto.user

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role
import java.util.UUID

data class UpdateUserRequest(
    val name: String,
    val role: Role,
    val jobTitle: JobTitle?,
    val sectorId: UUID?,
    val active: Boolean? = null
)
