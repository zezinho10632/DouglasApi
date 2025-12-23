package com.medTech.Douglas.api.dto.period

data class CreatePeriodRequest(
    val sectorId: String,
    val month: Int,
    val year: Int
)
