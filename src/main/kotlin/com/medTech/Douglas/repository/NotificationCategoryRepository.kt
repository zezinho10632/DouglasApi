package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.NotificationCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationCategoryRepository : JpaRepository<NotificationCategory, UUID> {
    fun findByName(name: String): NotificationCategory?
    fun findAllByActiveTrue(): List<NotificationCategory>
}
