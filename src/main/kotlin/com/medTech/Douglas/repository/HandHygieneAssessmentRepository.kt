package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.HandHygieneAssessment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HandHygieneAssessmentRepository : JpaRepository<HandHygieneAssessment, UUID> {
    fun findByPeriodId(periodId: UUID): HandHygieneAssessment?
}
