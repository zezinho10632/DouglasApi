package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.NotificationClassification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationClassificationRepository : JpaRepository<NotificationClassification, UUID> {
    fun findByName(name: String): NotificationClassification?
    fun findAllByActiveTrue(): List<NotificationClassification>
}
