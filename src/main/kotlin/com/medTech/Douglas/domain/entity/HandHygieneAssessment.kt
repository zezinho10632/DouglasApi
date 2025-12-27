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

    @Column(name = "compliance_percentage", nullable = false)
    var compliancePercentage: BigDecimal,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(compliancePercentage: BigDecimal) {
        this.compliancePercentage = compliancePercentage
        this.updatedAt = LocalDateTime.now()
    }

    companion object {
        fun create(
            periodId: UUID,
            sectorId: UUID,
            compliancePercentage: BigDecimal
        ): HandHygieneAssessment {
            return HandHygieneAssessment(
                periodId = periodId,
                sectorId = sectorId,
                compliancePercentage = compliancePercentage
            )
        }
    }
}
