package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.AdverseEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AdverseEventRepository : JpaRepository<AdverseEvent, UUID> {
    fun findByPeriodId(periodId: UUID): List<AdverseEvent>
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): List<AdverseEvent>
    fun findByPeriodIdAndSectorIdAndEventDateBetween(periodId: UUID, sectorId: UUID, startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<AdverseEvent>
    fun findByEventDateBetween(startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<AdverseEvent>
    fun findByCreatedBy(userId: UUID): List<AdverseEvent>
}
