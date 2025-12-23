package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.ComplianceIndicator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ComplianceIndicatorRepository : JpaRepository<ComplianceIndicator, UUID> {
    fun findByPeriodId(periodId: UUID): ComplianceIndicator?
}
