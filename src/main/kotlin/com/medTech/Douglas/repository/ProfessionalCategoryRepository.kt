package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.ProfessionalCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProfessionalCategoryRepository : JpaRepository<ProfessionalCategory, UUID> {
    fun findByName(name: String): ProfessionalCategory?
    fun findAllByActiveTrue(): List<ProfessionalCategory>
}
