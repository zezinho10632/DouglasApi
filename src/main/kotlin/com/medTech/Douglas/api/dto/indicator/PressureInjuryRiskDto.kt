package com.medTech.Douglas.api.dto.indicator

import java.math.BigDecimal

data class PressureInjuryRiskRequest(
    val periodId: String,
    val sectorId: String,
    val totalPatients: Int,
    val assessedOnAdmission: Int,
    val veryHigh: Int,
    val highRisk: Int,
    val mediumRisk: Int,
    val lowRisk: Int,
    val notAssessed: Int
)

data class PressureInjuryRiskResponse(
    val id: String,
    val periodId: String,
    val sectorId: String,
    val totalPatients: Int,
    val assessedOnAdmission: Int,
    val assessmentPercentage: BigDecimal,
    val veryHigh: Int,
    val highRisk: Int,
    val mediumRisk: Int,
    val lowRisk: Int,
    val notAssessed: Int,
    val veryHighPercentage: BigDecimal,
    val highRiskPercentage: BigDecimal,
    val mediumRiskPercentage: BigDecimal,
    val lowRiskPercentage: BigDecimal,
    val notAssessedPercentage: BigDecimal,
    val createdAt: String
)
