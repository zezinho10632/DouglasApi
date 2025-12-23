package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.AdverseEvent
import com.medTech.Douglas.domain.enums.EventType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AdverseEventRepository : JpaRepository<AdverseEvent, UUID> {
    fun findByPeriodId(periodId: UUID): List<AdverseEvent>
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): List<AdverseEvent>
    fun findByPeriodIdAndSectorIdAndEventDateBetween(periodId: UUID, sectorId: UUID, startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<AdverseEvent>
    fun findByEventDateBetween(startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<AdverseEvent>
    fun findByCreatedBy(userId: UUID): List<AdverseEvent>

    @Query("SELECT a FROM AdverseEvent a WHERE a.periodId = :periodId " +
           "AND (cast(:eventType as text) IS NULL OR a.eventType = :eventType) " +
           "ORDER BY a.eventDate DESC")
    fun search(periodId: UUID, eventType: EventType?): List<AdverseEvent>
}
