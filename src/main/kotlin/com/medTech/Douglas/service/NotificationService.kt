package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.notification.UpdateNotificationRequest
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.NotificationCategoryRepository
import com.medTech.Douglas.repository.NotificationClassificationRepository
import com.medTech.Douglas.repository.NotificationRepository
import com.medTech.Douglas.repository.ProfessionalCategoryRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.NotificationMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NotificationService(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
    private val classificationRepository: NotificationClassificationRepository,
    private val categoryRepository: NotificationCategoryRepository,
    private val professionalCategoryRepository: ProfessionalCategoryRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: NotificationMapper
) {

    @Transactional
    fun create(request: CreateNotificationRequest): NotificationResponse {
        periodValidator.validatePeriodIsOpen(request.periodId)
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        val notification = mapper.toDomain(request, user?.id)
        val saved = repository.save(notification)
        
        auditLogService.log("CREATE", "Notification", saved.id.toString(), "Created Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun listByPeriod(periodId: UUID, classificationId: UUID? = null, categoryId: UUID? = null): List<NotificationResponse> {
        val notifications = repository.search(periodId, classificationId, categoryId)
        return notifications.map { mapper.toResponse(it) }
    }

    @Transactional
    fun update(id: UUID, request: UpdateNotificationRequest): NotificationResponse {
        val notification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }

        periodValidator.validatePeriodIsOpen(notification.periodId)

        val classification = classificationRepository.findById(request.classificationId)
            .orElseThrow { ResourceNotFoundException("Classification not found") }
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { ResourceNotFoundException("Category not found") }
        
        val professionalCategory = request.professionalCategoryId?.let {
            professionalCategoryRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Professional Category not found") }
        }

        notification.classification = classification
        notification.category = category
        notification.professionalCategory = professionalCategory
        notification.quantityClassification = request.quantityClassification
        notification.quantityCategory = request.quantityCategory
        notification.quantityProfessional = request.quantityProfessional
        notification.updatedAt = java.time.LocalDateTime.now()

        val saved = repository.save(notification)
        
        auditLogService.log("UPDATE", "Notification", saved.id.toString(), "Updated Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): NotificationResponse {
        val notification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }
        
        return mapper.toResponse(notification)
    }
}
