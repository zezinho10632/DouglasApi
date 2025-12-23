package com.medTech.Douglas.api.dto.indicator

import java.math.BigDecimal

data class ComplianceIndicatorRequest(
    val periodId: String,
    val sectorId: String,
    val completeWristband: BigDecimal,
    val patientCommunication: BigDecimal,
    val medicationIdentified: BigDecimal,
    val handHygieneAdherence: BigDecimal,
    val fallRiskAssessment: BigDecimal,
    val pressureInjuryRiskAssessment: BigDecimal,
    val totalPatients: Int,
    val observations: String?
)

data class ComplianceIndicatorResponse(
    val id: String,
    val periodId: String,
    val sectorId: String,
    val completeWristband: BigDecimal,
    val patientCommunication: BigDecimal,
    val medicationIdentified: BigDecimal,
    val handHygieneAdherence: BigDecimal,
    val fallRiskAssessment: BigDecimal,
    val pressureInjuryRiskAssessment: BigDecimal,
    val totalPatients: Int,
    val observations: String?,
    val createdAt: String
)
