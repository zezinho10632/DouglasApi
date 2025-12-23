package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Period
import com.medTech.Douglas.domain.enums.PeriodStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PeriodRepository : JpaRepository<Period, UUID> {
    fun findBySectorIdAndMonthAndYear(sectorId: UUID, month: Int, year: Int): Period?
    fun findBySectorId(sectorId: UUID): List<Period>
    fun existsBySectorIdAndMonthAndYear(sectorId: UUID, month: Int, year: Int): Boolean
    fun findBySectorIdAndStatus(sectorId: UUID, status: PeriodStatus): Optional<Period>
    
    @Query("SELECT p FROM Period p WHERE p.sectorId = :sectorId " +
           "AND (cast(:status as text) IS NULL OR p.status = :status) " +
           "AND (cast(:year as integer) IS NULL OR p.year = :year) " +
           "ORDER BY p.year DESC, p.month DESC")
    fun search(sectorId: UUID, status: PeriodStatus?, year: Int?): List<Period>
}
