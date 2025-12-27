package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.SelfNotification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SelfNotificationRepository : JpaRepository<SelfNotification, UUID> {
    fun findByPeriodId(periodId: UUID): SelfNotification?
    fun findByPeriodIdAndSectorId(periodId: UUID, sectorId: UUID): SelfNotification?
}
