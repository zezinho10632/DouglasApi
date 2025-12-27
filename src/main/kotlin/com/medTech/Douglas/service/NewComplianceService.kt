package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.compliance.*
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.MedicationComplianceRepository
import com.medTech.Douglas.repository.MetaComplianceRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.NewComplianceMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NewComplianceService(
    private val metaRepository: MetaComplianceRepository,
    private val medicationRepository: MedicationComplianceRepository,
    private val userRepository: UserRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: NewComplianceMapper
) {

    // Meta Compliance
    @Transactional
    fun createMetaCompliance(request: CreateMetaComplianceRequest): MetaComplianceResponse {
        periodValidator.validatePeriodIsOpen(request.periodId)
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        
        val domain = mapper.toDomain(request, user?.id)
        val saved = metaRepository.save(domain)
        
        auditLogService.log("CREATE", "MetaCompliance", saved.id.toString(), "Created Meta Compliance")
        
        return mapper.toResponse(saved)
    }

    @Transactional
    fun updateMetaCompliance(id: UUID, request: UpdateMetaComplianceRequest): MetaComplianceResponse {
        val domain = metaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("MetaCompliance not found with id: $id") }

        periodValidator.validatePeriodIsOpen(domain.periodId)

        domain.goalValue = request.goalValue
        domain.percentage = request.percentage
        domain.updatedAt = java.time.LocalDateTime.now()

        val saved = metaRepository.save(domain)
        
        auditLogService.log("UPDATE", "MetaCompliance", saved.id.toString(), "Updated Meta Compliance")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun findMetaComplianceByPeriodId(periodId: UUID): MetaComplianceResponse? {
        val domain = metaRepository.findByPeriodId(periodId) ?: return null
        return mapper.toResponse(domain)
    }

    // Medication Compliance
    @Transactional
    fun createMedicationCompliance(request: CreateMedicationComplianceRequest): MedicationComplianceResponse {
        periodValidator.validatePeriodIsOpen(request.periodId)
        val email = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByEmail(email)
        
        val domain = mapper.toDomain(request, user?.id)
        val saved = medicationRepository.save(domain)
        
        auditLogService.log("CREATE", "MedicationCompliance", saved.id.toString(), "Created Medication Compliance")
        
        return mapper.toResponse(saved)
    }

    @Transactional
    fun updateMedicationCompliance(id: UUID, request: UpdateMedicationComplianceRequest): MedicationComplianceResponse {
        val domain = medicationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("MedicationCompliance not found with id: $id") }

        periodValidator.validatePeriodIsOpen(domain.periodId)

        domain.percentage = request.percentage
        domain.updatedAt = java.time.LocalDateTime.now()

        val saved = medicationRepository.save(domain)
        
        auditLogService.log("UPDATE", "MedicationCompliance", saved.id.toString(), "Updated Medication Compliance")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun findMedicationComplianceByPeriodId(periodId: UUID): MedicationComplianceResponse? {
        val domain = medicationRepository.findByPeriodId(periodId) ?: return null
        return mapper.toResponse(domain)
    }
}
