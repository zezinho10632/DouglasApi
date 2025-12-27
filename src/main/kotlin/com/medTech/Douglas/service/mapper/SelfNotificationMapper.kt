package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.selfnotification.CreateSelfNotificationRequest
import com.medTech.Douglas.api.dto.selfnotification.SelfNotificationResponse
import com.medTech.Douglas.domain.entity.SelfNotification
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SelfNotificationMapper {
    fun toDomain(request: CreateSelfNotificationRequest, userId: UUID?): SelfNotification {
        return SelfNotification(
            periodId = request.periodId,
            sectorId = request.sectorId,
            quantity = request.quantity,
            percentage = request.percentage,
            createdBy = userId
        )
    }

    fun toResponse(domain: SelfNotification): SelfNotificationResponse {
        return SelfNotificationResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            quantity = domain.quantity,
            percentage = domain.percentage,
            createdBy = domain.createdBy
        )
    }
}
