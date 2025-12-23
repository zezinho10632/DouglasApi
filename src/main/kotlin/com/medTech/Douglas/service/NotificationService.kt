package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.notification.UpdateNotificationRequest
import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.ClosedPeriodException
import com.medTech.Douglas.exception.PeriodNotFoundException
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.NotificationRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.NotificationMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

import com.medTech.Douglas.domain.enums.NotificationClassification

@Service
class NotificationService(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: NotificationMapper
) {

    @Transactional
    fun create(request: CreateNotificationRequest): NotificationResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        val notification = mapper.toDomain(request, user?.id)
        val saved = repository.save(notification)
        
        auditLogService.log("CREATE", "Notification", saved.id.toString(), "Created Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun listByPeriod(periodId: UUID, classification: NotificationClassification? = null, category: String? = null): List<NotificationResponse> {
        val notifications = repository.search(periodId, classification, category)
        val userIds = notifications.mapNotNull { it.createdBy }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { it.id }
        
        return notifications.map { mapper.toResponse(it, users) }
    }

    @Transactional
    fun update(id: UUID, request: UpdateNotificationRequest): NotificationResponse {
        val notification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }

        periodValidator.validatePeriodIsOpen(notification.periodId)

        notification.update(
            notificationDate = request.notificationDate,
            classification = request.classification,
            category = request.category,
            subcategory = request.subcategory,
            description = request.description,
            isSelfNotification = request.isSelfNotification,
            professionalCategory = request.professionalCategory,
            professionalName = request.professionalName
        )

        val saved = repository.save(notification)
        
        auditLogService.log("UPDATE", "Notification", saved.id.toString(), "Updated Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        val notification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }
        
        periodValidator.validatePeriodIsOpen(notification.periodId)
        
        repository.deleteById(id)
        
        auditLogService.log("DELETE", "Notification", id.toString(), "Deleted Notification")
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): NotificationResponse {
        val notification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }
        
        val userMap = notification.createdBy?.let { 
             userRepository.findById(it).orElse(null)?.let { user -> mapOf(user.id to user) }
        } ?: emptyMap()
        
        return mapper.toResponse(notification, userMap)
    }
}
