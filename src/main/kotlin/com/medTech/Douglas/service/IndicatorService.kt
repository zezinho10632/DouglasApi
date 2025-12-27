package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.indicator.*
import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.ClosedPeriodException
import com.medTech.Douglas.exception.PeriodNotFoundException
import com.medTech.Douglas.repository.ComplianceIndicatorRepository
import com.medTech.Douglas.repository.FallRiskAssessmentRepository
import com.medTech.Douglas.repository.HandHygieneAssessmentRepository
import com.medTech.Douglas.repository.PressureInjuryRiskAssessmentRepository
import com.medTech.Douglas.service.mapper.IndicatorMapper
import com.medTech.Douglas.service.validation.PeriodValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class IndicatorService(
    private val complianceRepository: ComplianceIndicatorRepository,
    private val fallRiskRepository: FallRiskAssessmentRepository,
    private val handHygieneRepository: HandHygieneAssessmentRepository,
    private val pressureInjuryRepository: PressureInjuryRiskAssessmentRepository,
    private val periodValidator: PeriodValidator,
    private val auditLogService: AuditLogService,
    private val mapper: IndicatorMapper
) {

    // Compliance
    @Transactional
    fun saveCompliance(request: ComplianceIndicatorRequest): ComplianceIndicatorResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val domain = mapper.toDomain(request)
        val saved = complianceRepository.save(domain)
        
        auditLogService.log("CREATE", "ComplianceIndicator", saved.id.toString(), "Created Compliance Indicator")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getComplianceByPeriod(periodId: UUID): ComplianceIndicatorResponse? {
        return complianceRepository.findByPeriodId(periodId)?.let { mapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getComplianceById(id: UUID): ComplianceIndicatorResponse? {
        return complianceRepository.findById(id).orElse(null)?.let { mapper.toResponse(it) }
    }

    @Transactional
    fun updateCompliance(id: UUID, request: ComplianceIndicatorRequest): ComplianceIndicatorResponse {
        val indicator = complianceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Indicator not found with id: $id") }
        
        periodValidator.validatePeriodIsOpen(indicator.periodId)
        
        indicator.update(
            completeWristband = request.completeWristband,
            patientCommunication = request.patientCommunication,
            medicationIdentified = request.medicationIdentified,
            handHygieneAdherence = request.handHygieneAdherence,
            fallRiskAssessment = request.fallRiskAssessment,
            pressureInjuryRiskAssessment = request.pressureInjuryRiskAssessment,
            observations = request.observations
        )
        
        val saved = complianceRepository.save(indicator)
        
        auditLogService.log("UPDATE", "ComplianceIndicator", saved.id.toString(), "Updated Compliance Indicator")
        
        return mapper.toResponse(saved)
    }

    // Hand Hygiene
    @Transactional
    fun saveHandHygiene(request: HandHygieneRequest): HandHygieneResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val domain = mapper.toDomain(request)
        val saved = handHygieneRepository.save(domain)
        
        auditLogService.log("CREATE", "HandHygieneAssessment", saved.id.toString(), "Created Hand Hygiene Assessment")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getHandHygieneByPeriod(periodId: UUID): HandHygieneResponse? {
        return handHygieneRepository.findByPeriodId(periodId)?.let { mapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getHandHygieneById(id: UUID): HandHygieneResponse? {
        return handHygieneRepository.findById(id).orElse(null)?.let { mapper.toResponse(it) }
    }

    @Transactional
    fun updateHandHygiene(id: UUID, request: HandHygieneRequest): HandHygieneResponse {
        val indicator = handHygieneRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Indicator not found with id: $id") }
            
        periodValidator.validatePeriodIsOpen(indicator.periodId)
        
        indicator.update(
            totalObservations = request.totalObservations,
            compliantObservations = request.compliantObservations
        )
        
        val saved = handHygieneRepository.save(indicator)
        
        auditLogService.log("UPDATE", "HandHygieneAssessment", saved.id.toString(), "Updated Hand Hygiene Assessment")
        
        return mapper.toResponse(saved)
    }

    // Fall Risk
    @Transactional
    fun saveFallRisk(request: FallRiskRequest): FallRiskResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val domain = mapper.toDomain(request)
        val saved = fallRiskRepository.save(domain)
        
        auditLogService.log("CREATE", "FallRiskAssessment", saved.id.toString(), "Created Fall Risk Assessment")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getFallRiskByPeriod(periodId: UUID): FallRiskResponse? {
        return fallRiskRepository.findByPeriodId(periodId)?.let { mapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getFallRiskById(id: UUID): FallRiskResponse? {
        return fallRiskRepository.findById(id).orElse(null)?.let { mapper.toResponse(it) }
    }

    @Transactional
    fun updateFallRisk(id: UUID, request: FallRiskRequest): FallRiskResponse {
        val indicator = fallRiskRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Indicator not found with id: $id") }
            
        periodValidator.validatePeriodIsOpen(indicator.periodId)
        
        indicator.update(
            totalPatients = request.totalPatients,
            assessedOnAdmission = request.assessedOnAdmission,
            highRisk = request.highRisk,
            mediumRisk = request.mediumRisk,
            lowRisk = request.lowRisk,
            notAssessed = request.notAssessed
        )
        
        val saved = fallRiskRepository.save(indicator)
        
        auditLogService.log("UPDATE", "FallRiskAssessment", saved.id.toString(), "Updated Fall Risk Assessment")
        
        return mapper.toResponse(saved)
    }

    // Pressure Injury
    @Transactional
    fun savePressureInjuryRisk(request: PressureInjuryRiskRequest): PressureInjuryRiskResponse {
        periodValidator.validatePeriodIsOpen(UUID.fromString(request.periodId))
        val domain = mapper.toDomain(request)
        val saved = pressureInjuryRepository.save(domain)
        
        auditLogService.log("CREATE", "PressureInjuryRiskAssessment", saved.id.toString(), "Created Pressure Injury Risk Assessment")
        
        return mapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getPressureInjuryRiskByPeriod(periodId: UUID): PressureInjuryRiskResponse? {
        return pressureInjuryRepository.findByPeriodId(periodId)?.let { mapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getPressureInjuryRiskById(id: UUID): PressureInjuryRiskResponse? {
        return pressureInjuryRepository.findById(id).orElse(null)?.let { mapper.toResponse(it) }
    }

    @Transactional
    fun updatePressureInjuryRisk(id: UUID, request: PressureInjuryRiskRequest): PressureInjuryRiskResponse {
        val indicator = pressureInjuryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Indicator not found with id: $id") }
            
        periodValidator.validatePeriodIsOpen(indicator.periodId)
        
        indicator.update(
            totalPatients = request.totalPatients,
            assessedOnAdmission = request.assessedOnAdmission,
            highRisk = request.highRisk,
            mediumRisk = request.mediumRisk,
            lowRisk = request.lowRisk,
            notAssessed = request.notAssessed
        )
        
        val saved = pressureInjuryRepository.save(indicator)
        
        auditLogService.log("UPDATE", "PressureInjuryRiskAssessment", saved.id.toString(), "Updated Pressure Injury Risk Assessment")
        
        return mapper.toResponse(saved)
    }
}
