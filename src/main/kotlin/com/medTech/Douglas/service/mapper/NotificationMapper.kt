package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.category.CategoryResponse
import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import com.medTech.Douglas.domain.entity.Notification
import com.medTech.Douglas.repository.NotificationCategoryRepository
import com.medTech.Douglas.repository.NotificationClassificationRepository
import com.medTech.Douglas.repository.ProfessionalCategoryRepository
import com.medTech.Douglas.exception.ResourceNotFoundException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationMapper(
    private val classificationRepository: NotificationClassificationRepository,
    private val categoryRepository: NotificationCategoryRepository,
    private val professionalCategoryRepository: ProfessionalCategoryRepository
) {
    fun toDomain(request: CreateNotificationRequest, userId: UUID? = null): Notification {
        val classification = classificationRepository.findById(request.classificationId)
            .orElseThrow { ResourceNotFoundException("Classification not found") }
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { ResourceNotFoundException("Category not found") }
        
        val professionalCategory = request.professionalCategoryId?.let {
            professionalCategoryRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Professional Category not found") }
        }

        return Notification(
            periodId = request.periodId,
            sectorId = request.sectorId,
            classification = classification,
            category = category,
            professionalCategory = professionalCategory,
            isSelfNotification = request.isSelfNotification,
            quantity = request.quantity,
            createdBy = userId
        )
    }

    fun toResponse(domain: Notification): NotificationResponse {
        return NotificationResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            classification = ClassificationResponse(domain.classification.id, domain.classification.name, domain.classification.active),
            category = CategoryResponse(domain.category.id, domain.category.name, domain.category.active),
            professionalCategory = domain.professionalCategory?.let { ProfessionalCategoryResponse(it.id, it.name, it.active) },
            isSelfNotification = domain.isSelfNotification,
            quantity = domain.quantity,
            createdBy = domain.createdBy,
            createdAt = domain.createdAt
        )
    }
}
