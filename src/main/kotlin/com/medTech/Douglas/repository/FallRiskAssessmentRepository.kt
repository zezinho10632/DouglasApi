package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.FallRiskAssessment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FallRiskAssessmentRepository : JpaRepository<FallRiskAssessment, UUID> {
    fun findByPeriodId(periodId: UUID): FallRiskAssessment?
    fun findBySectorId(sectorId: UUID): List<FallRiskAssessment>
}
