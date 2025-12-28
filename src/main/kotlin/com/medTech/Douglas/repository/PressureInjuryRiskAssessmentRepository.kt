package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.PressureInjuryRiskAssessment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PressureInjuryRiskAssessmentRepository : JpaRepository<PressureInjuryRiskAssessment, UUID> {
    fun findByPeriodId(periodId: UUID): PressureInjuryRiskAssessment?
    fun findBySectorId(sectorId: UUID): List<PressureInjuryRiskAssessment>
}
