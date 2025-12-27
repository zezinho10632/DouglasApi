package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.selfnotification.CreateSelfNotificationRequest
import com.medTech.Douglas.api.dto.selfnotification.SelfNotificationResponse
import com.medTech.Douglas.api.dto.selfnotification.UpdateSelfNotificationRequest
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.SelfNotificationRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.SelfNotificationMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SelfNotificationService(
    private val repository: SelfNotificationRepository,
    private val userRepository: UserRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: SelfNotificationMapper
) {

    @Transactional
    fun create(request: CreateSelfNotificationRequest): SelfNotificationResponse {
        periodValidator.validatePeriodIsOpen(request.periodId)
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        
        val selfNotification = mapper.toDomain(request, user?.id)
        val saved = repository.save(selfNotification)
        
        auditLogService.log("CREATE", "SelfNotification", saved.id.toString(), "Created Self Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional
    fun update(id: UUID, request: UpdateSelfNotificationRequest): SelfNotificationResponse {
        val selfNotification = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("SelfNotification not found with id: $id") }

        periodValidator.validatePeriodIsOpen(selfNotification.periodId)

        selfNotification.quantity = request.quantity
        selfNotification.percentage = request.percentage
        selfNotification.updatedAt = java.time.LocalDateTime.now()

        val saved = repository.save(selfNotification)
        
        auditLogService.log("UPDATE", "SelfNotification", saved.id.toString(), "Updated Self Notification")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun findByPeriodId(periodId: UUID): SelfNotificationResponse? {
        val selfNotification = repository.findByPeriodId(periodId) ?: return null
        return mapper.toResponse(selfNotification)
    }
}
