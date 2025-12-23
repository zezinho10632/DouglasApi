package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SectorRepository : JpaRepository<Sector, UUID> {
    fun findByCode(code: String): Sector?
    fun findAllByActiveTrue(): List<Sector>
    fun existsByCode(code: String): Boolean
}
