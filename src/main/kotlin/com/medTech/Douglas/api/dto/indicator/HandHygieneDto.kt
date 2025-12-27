package com.medTech.Douglas.api.dto.indicator

import java.math.BigDecimal

data class HandHygieneRequest(
    val periodId: String,
    val sectorId: String,
    val compliancePercentage: BigDecimal
)

data class HandHygieneResponse(
    val id: String,
    val periodId: String,
    val sectorId: String,
    val compliancePercentage: BigDecimal,
    val createdAt: String
)
