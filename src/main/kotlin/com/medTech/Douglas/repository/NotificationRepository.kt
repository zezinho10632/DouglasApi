package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByPeriodId(periodId: UUID): List<Notification>
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): List<Notification>
    fun findByCreatedBy(userId: UUID): List<Notification>

    @Query("SELECT n FROM Notification n WHERE n.periodId = :periodId " +
           "AND (cast(:classificationId as text) IS NULL OR n.classification.id = :classificationId) " +
           "AND (cast(:categoryId as text) IS NULL OR n.category.id = :categoryId) ")
    fun search(periodId: UUID, classificationId: UUID?, categoryId: UUID?): List<Notification>
}
