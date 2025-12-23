package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Period
import com.medTech.Douglas.domain.enums.PeriodStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PeriodRepository : JpaRepository<Period, UUID> {
    fun findBySectorIdAndMonthAndYear(sectorId: UUID, month: Int, year: Int): Period?
    fun findBySectorId(sectorId: UUID): List<Period>
    fun existsBySectorIdAndMonthAndYear(sectorId: UUID, month: Int, year: Int): Boolean
    fun findBySectorIdAndStatus(sectorId: UUID, status: PeriodStatus): Optional<Period>
    
    // Custom query to find periods within a date range logic would be complex with just month/year fields
    // Simplest approach: fetch all by sector and filter in memory, or add @Query if needed.
    // Given the scale, fetching all by sector is acceptable for now, but let's add ordering.
    fun findBySectorIdOrderByYearDescMonthDesc(sectorId: UUID): List<Period>
}
