package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByPeriodId(periodId: UUID): List<Notification>
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): List<Notification>
    fun findByPeriodIdAndSectorIdAndNotificationDateBetween(periodId: UUID, sectorId: UUID, startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<Notification>
    fun findByNotificationDateBetween(startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<Notification>
    fun findByCreatedBy(userId: UUID): List<Notification>
}
