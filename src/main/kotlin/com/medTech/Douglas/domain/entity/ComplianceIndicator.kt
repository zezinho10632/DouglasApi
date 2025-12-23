package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "compliance_indicators")
class ComplianceIndicator(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false, unique = true)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Column(name = "complete_wristband", nullable = false)
    var completeWristband: BigDecimal,

    @Column(name = "patient_communication", nullable = false)
    var patientCommunication: BigDecimal,

    @Column(name = "medication_identified", nullable = false)
    var medicationIdentified: BigDecimal,

    @Column(name = "hand_hygiene_adherence", nullable = false)
    var handHygieneAdherence: BigDecimal,

    @Column(name = "fall_risk_assessment", nullable = false)
    var fallRiskAssessment: BigDecimal,

    @Column(name = "pressure_injury_risk_assessment", nullable = false)
    var pressureInjuryRiskAssessment: BigDecimal,

    @Column(name = "total_patients", nullable = false)
    var totalPatients: Int,

    @Column(columnDefinition = "TEXT")
    var observations: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(
        completeWristband: BigDecimal,
        patientCommunication: BigDecimal,
        medicationIdentified: BigDecimal,
        handHygieneAdherence: BigDecimal,
        fallRiskAssessment: BigDecimal,
        pressureInjuryRiskAssessment: BigDecimal,
        totalPatients: Int,
        observations: String?
    ) {
        this.completeWristband = completeWristband
        this.patientCommunication = patientCommunication
        this.medicationIdentified = medicationIdentified
        this.handHygieneAdherence = handHygieneAdherence
        this.fallRiskAssessment = fallRiskAssessment
        this.pressureInjuryRiskAssessment = pressureInjuryRiskAssessment
        this.totalPatients = totalPatients
        this.observations = observations
        this.updatedAt = LocalDateTime.now()
    }
}
