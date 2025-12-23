package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "hand_hygiene_assessments")
class HandHygieneAssessment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false, unique = true)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Column(name = "total_observations", nullable = false)
    var totalObservations: Int,

    @Column(name = "compliant_observations", nullable = false)
    var compliantObservations: Int,

    @Column(name = "compliance_percentage", nullable = false)
    var compliancePercentage: BigDecimal,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(totalObservations: Int, compliantObservations: Int) {
        this.totalObservations = totalObservations
        this.compliantObservations = compliantObservations
        this.compliancePercentage = calculatePercentage(totalObservations, compliantObservations)
        this.updatedAt = LocalDateTime.now()
    }

    private fun calculatePercentage(total: Int, compliant: Int): BigDecimal {
        if (total == 0) return BigDecimal.ZERO
        return BigDecimal.valueOf(compliant.toLong())
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(total.toLong()), 2, RoundingMode.HALF_UP)
    }

    companion object {
        fun create(
            periodId: UUID,
            sectorId: UUID,
            totalObservations: Int,
            compliantObservations: Int
        ): HandHygieneAssessment {
            val percentage = if (totalObservations == 0) BigDecimal.ZERO else BigDecimal.valueOf(compliantObservations.toLong())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalObservations.toLong()), 2, RoundingMode.HALF_UP)
            
            return HandHygieneAssessment(
                periodId = periodId,
                sectorId = sectorId,
                totalObservations = totalObservations,
                compliantObservations = compliantObservations,
                compliancePercentage = percentage
            )
        }
    }
}
