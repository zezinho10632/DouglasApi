package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "fall_risk_assessments")
class FallRiskAssessment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false, unique = true)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Column(name = "total_patients", nullable = false)
    var totalPatients: Int,

    @Column(name = "assessed_on_admission", nullable = false)
    var assessedOnAdmission: Int,

    @Column(name = "assessment_percentage", nullable = false)
    var assessmentPercentage: BigDecimal,

    @Column(name = "high_risk", nullable = false)
    var highRisk: Int = 0,

    @Column(name = "medium_risk", nullable = false)
    var mediumRisk: Int = 0,

    @Column(name = "low_risk", nullable = false)
    var lowRisk: Int = 0,

    @Column(name = "not_assessed", nullable = false)
    var notAssessed: Int = 0,

    @Column(name = "high_risk_percentage", nullable = false)
    var highRiskPercentage: BigDecimal = BigDecimal.ZERO,

    @Column(name = "medium_risk_percentage", nullable = false)
    var mediumRiskPercentage: BigDecimal = BigDecimal.ZERO,

    @Column(name = "low_risk_percentage", nullable = false)
    var lowRiskPercentage: BigDecimal = BigDecimal.ZERO,

    @Column(name = "not_assessed_percentage", nullable = false)
    var notAssessedPercentage: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(
        totalPatients: Int,
        assessedOnAdmission: Int,
        highRisk: Int,
        mediumRisk: Int,
        lowRisk: Int,
        notAssessed: Int
    ) {
        this.totalPatients = totalPatients
        this.assessedOnAdmission = assessedOnAdmission
        this.assessmentPercentage = calculatePercentage(totalPatients, assessedOnAdmission)
        
        this.highRisk = highRisk
        this.mediumRisk = mediumRisk
        this.lowRisk = lowRisk
        this.notAssessed = notAssessed
        
        this.highRiskPercentage = calculatePercentage(totalPatients, highRisk)
        this.mediumRiskPercentage = calculatePercentage(totalPatients, mediumRisk)
        this.lowRiskPercentage = calculatePercentage(totalPatients, lowRisk)
        this.notAssessedPercentage = calculatePercentage(totalPatients, notAssessed)
        
        this.updatedAt = LocalDateTime.now()
    }
    
    private fun calculatePercentage(total: Int, value: Int): BigDecimal {
        if (total == 0) return BigDecimal.ZERO
        return BigDecimal.valueOf(value.toLong())
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(total.toLong()), 2, RoundingMode.HALF_UP)
    }

    companion object {
        fun create(
            periodId: UUID,
            sectorId: UUID,
            totalPatients: Int,
            assessedOnAdmission: Int,
            highRisk: Int,
            mediumRisk: Int,
            lowRisk: Int,
            notAssessed: Int
        ): FallRiskAssessment {
            val assessment = FallRiskAssessment(
                periodId = periodId,
                sectorId = sectorId,
                totalPatients = totalPatients,
                assessedOnAdmission = assessedOnAdmission,
                assessmentPercentage = BigDecimal.ZERO, // Placeholder, updated below
                highRisk = highRisk,
                mediumRisk = mediumRisk,
                lowRisk = lowRisk,
                notAssessed = notAssessed
            )
            
            // Trigger calculation
            assessment.update(
                totalPatients,
                assessedOnAdmission,
                highRisk,
                mediumRisk,
                lowRisk,
                notAssessed
            )
            
            return assessment
        }
    }
}
