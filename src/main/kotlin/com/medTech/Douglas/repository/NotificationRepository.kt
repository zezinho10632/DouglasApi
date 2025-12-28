package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByPeriodId(periodId: UUID): List<Notification>
    fun findBySectorId(sectorId: UUID): List<Notification>
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): List<Notification>
    fun findByCreatedBy(userId: UUID): List<Notification>

    @Query("SELECT n FROM Notification n WHERE n.periodId = :periodId " +
           "AND (cast(:classificationId as text) IS NULL OR n.classification.id = :classificationId) ")
    fun search(periodId: UUID, classificationId: UUID?): List<Notification>

    @Query("SELECT new com.medTech.Douglas.api.dto.notification.ProfessionalCategoryRankingResponse(" +
           "COALESCE(pc.name, n.professionalCategoryText, 'Não Informado'), SUM(n.quantityProfessional)) " +
           "FROM Notification n LEFT JOIN n.professionalCategory pc " +
           "WHERE (:periodId IS NULL OR n.periodId = :periodId) " +
           "AND (:sectorId IS NULL OR n.sectorId = :sectorId) " +
           "GROUP BY COALESCE(pc.name, n.professionalCategoryText, 'Não Informado') " +
           "ORDER BY SUM(n.quantityProfessional) DESC")
    fun getProfessionalCategoryRanking(periodId: UUID?, sectorId: UUID?): List<com.medTech.Douglas.api.dto.notification.ProfessionalCategoryRankingResponse>
}
