package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.MedicationCompliance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MedicationComplianceRepository : JpaRepository<MedicationCompliance, UUID> {
    fun findByPeriodId(periodId: UUID): MedicationCompliance?
}
