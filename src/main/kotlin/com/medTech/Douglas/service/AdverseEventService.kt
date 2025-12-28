package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.api.dto.adverseevent.CreateAdverseEventRequest
import com.medTech.Douglas.api.dto.adverseevent.UpdateAdverseEventRequest
import com.medTech.Douglas.domain.enums.EventType
import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.ClosedPeriodException
import com.medTech.Douglas.exception.PeriodNotFoundException
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.AdverseEventRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.AdverseEventMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdverseEventService(
    private val repository: AdverseEventRepository,
    private val userRepository: UserRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: AdverseEventMapper
) {

    @Transactional
    fun create(request: CreateAdverseEventRequest): AdverseEventResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        val domain = mapper.toDomain(request, user?.id)
        val saved = repository.save(domain)
        
        auditLogService.log("CREATE", "AdverseEvent", saved.id.toString(), "Created Adverse Event")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun listByPeriod(periodId: UUID, eventType: EventType? = null): List<AdverseEventResponse> {
        val events = repository.search(periodId, eventType)
        val userIds = events.mapNotNull { it.createdBy }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { it.id }
        
        return events.map { mapper.toResponse(it, users) }
    }

    @Transactional(readOnly = true)
    fun listBySector(sectorId: UUID): List<AdverseEventResponse> {
        val events = repository.findBySectorId(sectorId)
        val userIds = events.mapNotNull { it.createdBy }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { it.id }
        
        return events.map { mapper.toResponse(it, users) }
    }

    @Transactional
    fun update(id: UUID, request: UpdateAdverseEventRequest): AdverseEventResponse {
        val adverseEvent = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Adverse Event not found with id: $id") }

        periodValidator.validatePeriodIsOpen(adverseEvent.periodId)

        adverseEvent.update(
            eventDate = request.eventDate,
            eventType = EventType.valueOf(request.eventType),
            description = request.description,
            quantityCases = request.quantityCases,
            quantityNotifications = request.quantityNotifications
        )

        val saved = repository.save(adverseEvent)
        
        auditLogService.log("UPDATE", "AdverseEvent", saved.id.toString(), "Updated Adverse Event")
        
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        val adverseEvent = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Adverse Event not found with id: $id") }
        
        periodValidator.validatePeriodIsOpen(adverseEvent.periodId)
        
        repository.deleteById(id)
        
        auditLogService.log("DELETE", "AdverseEvent", id.toString(), "Deleted Adverse Event")
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): AdverseEventResponse {
        val adverseEvent = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Adverse Event not found with id: $id") }
        
        val userMap = adverseEvent.createdBy?.let { 
             userRepository.findById(it).orElse(null)?.let { user -> mapOf(user.id to user) }
        } ?: emptyMap()
        
        return mapper.toResponse(adverseEvent, userMap)
    }
}
