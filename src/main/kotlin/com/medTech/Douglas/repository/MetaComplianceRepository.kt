package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.MetaCompliance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MetaComplianceRepository : JpaRepository<MetaCompliance, UUID> {
    fun findByPeriodId(periodId: UUID): MetaCompliance?
}
