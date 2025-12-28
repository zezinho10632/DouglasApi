package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.api.dto.adverseevent.CreateAdverseEventRequest
import com.medTech.Douglas.domain.entity.AdverseEvent
import com.medTech.Douglas.domain.enums.EventType
import com.medTech.Douglas.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AdverseEventMapper(
    private val userRepository: UserRepository
) {
    fun toDomain(request: CreateAdverseEventRequest, userId: UUID? = null): AdverseEvent {
        return AdverseEvent(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            eventDate = request.eventDate,
            eventType = EventType.valueOf(request.eventType),
            description = request.description,
            quantityCases = request.quantityCases,
            quantityNotifications = request.quantityNotifications,
            createdBy = userId
        )
    }

    fun toResponse(domain: AdverseEvent): AdverseEventResponse {
        val user = domain.createdBy?.let { userRepository.findById(it).orElse(null) }
        return toResponse(domain, user)
    }

    fun toResponse(domain: AdverseEvent, user: com.medTech.Douglas.domain.entity.User?): AdverseEventResponse {
        return AdverseEventResponse(
            id = domain.id.toString(),
            periodId = domain.periodId.toString(),
            sectorId = domain.sectorId.toString(),
            eventDate = domain.eventDate,
            eventType = domain.eventType.name,
            description = domain.description,
            quantityCases = domain.quantityCases,
            quantityNotifications = domain.quantityNotifications,
            createdByName = user?.name,
            createdByJobTitle = user?.jobTitle,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toResponse(domain: AdverseEvent, usersMap: Map<UUID, com.medTech.Douglas.domain.entity.User>): AdverseEventResponse {
        val user = domain.createdBy?.let { usersMap[it] }
        return toResponse(domain, user)
    }
}
