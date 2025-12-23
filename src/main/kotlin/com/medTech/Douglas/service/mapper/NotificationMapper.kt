package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.domain.entity.Notification
import com.medTech.Douglas.domain.enums.NotificationClassification
import com.medTech.Douglas.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationMapper(
    private val userRepository: UserRepository
) {
    fun toDomain(request: CreateNotificationRequest, userId: UUID? = null): Notification {
        return Notification(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            notificationDate = request.notificationDate,
            classification = request.classification,
            category = request.category,
            subcategory = request.subcategory,
            description = request.description,
            isSelfNotification = request.isSelfNotification,
            professionalCategory = request.professionalCategory,
            professionalName = request.professionalName,
            createdBy = userId
        )
    }

    fun toResponse(domain: Notification): NotificationResponse {
        val user = domain.createdBy?.let { userRepository.findById(it).orElse(null) }
        return toResponse(domain, user)
    }

    fun toResponse(domain: Notification, user: com.medTech.Douglas.domain.entity.User?): NotificationResponse {
        return NotificationResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            notificationDate = domain.notificationDate,
            classification = domain.classification,
            category = domain.category,
            subcategory = domain.subcategory,
            description = domain.description,
            isSelfNotification = domain.isSelfNotification,
            professionalCategory = domain.professionalCategory,
            professionalName = domain.professionalName,
            createdByName = user?.name,
            createdByJobTitle = user?.jobTitle,
            createdAt = domain.createdAt
        )
    }

    fun toResponse(domain: Notification, usersMap: Map<UUID, com.medTech.Douglas.domain.entity.User>): NotificationResponse {
        val user = domain.createdBy?.let { usersMap[it] }
        return toResponse(domain, user)
    }
}
