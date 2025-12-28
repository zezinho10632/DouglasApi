package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import com.medTech.Douglas.domain.entity.Notification
import com.medTech.Douglas.repository.NotificationClassificationRepository
import com.medTech.Douglas.repository.ProfessionalCategoryRepository
import com.medTech.Douglas.exception.ResourceNotFoundException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationMapper(
    private val classificationRepository: NotificationClassificationRepository,
    private val professionalCategoryRepository: ProfessionalCategoryRepository
) {
    fun toDomain(request: CreateNotificationRequest, userId: UUID? = null): Notification {
        val classification = request.classificationId?.let {
            classificationRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Classification not found") }
        }
        
        val professionalCategory = request.professionalCategoryId?.let {
            professionalCategoryRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Professional Category not found") }
        }

        return Notification(
            periodId = request.periodId,
            sectorId = request.sectorId,
            classification = classification,
            classificationText = request.classificationText,
            description = request.description,
            professionalCategory = professionalCategory,
            professionalCategoryText = request.professionalCategoryText,
            quantityClassification = request.quantityClassification,
            quantityCategory = request.quantityCategory,
            quantityProfessional = request.quantityProfessional,
            quantity = request.quantity,
            createdBy = userId
        )
    }

    fun toResponse(domain: Notification): NotificationResponse {
        return NotificationResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            classification = domain.classification?.let { ClassificationResponse(it.id, it.name, it.active) },
            classificationText = domain.classificationText,
            description = domain.description,
            professionalCategory = domain.professionalCategory?.let { ProfessionalCategoryResponse(it.id, it.name, it.active) },
            professionalCategoryText = domain.professionalCategoryText,
            quantityClassification = domain.quantityClassification,
            quantityCategory = domain.quantityCategory,
            quantityProfessional = domain.quantityProfessional,
            quantity = domain.quantity,
            createdBy = domain.createdBy,
            createdAt = domain.createdAt
        )
    }
}
