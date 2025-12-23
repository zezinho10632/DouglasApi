package com.medTech.Douglas.api.dto.period

import java.time.LocalDateTime
import java.util.UUID

data class PeriodResponse(
    val id: UUID,
    val sectorId: UUID,
    val month: Int,
    val year: Int,
    val active: Boolean,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
