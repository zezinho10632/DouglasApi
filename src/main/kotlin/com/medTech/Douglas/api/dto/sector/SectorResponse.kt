package com.medTech.Douglas.api.dto.sector

import java.time.LocalDateTime
import java.util.UUID

data class SectorResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
