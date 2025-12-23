package com.medTech.Douglas.api.dto.indicator

import java.math.BigDecimal

data class HandHygieneRequest(
    val periodId: String,
    val sectorId: String,
    val totalObservations: Int,
    val compliantObservations: Int
)

data class HandHygieneResponse(
    val id: String,
    val periodId: String,
    val sectorId: String,
    val totalObservations: Int,
    val compliantObservations: Int,
    val compliancePercentage: BigDecimal,
    val createdAt: String
)
