package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.indicator.*
import com.medTech.Douglas.domain.entity.ComplianceIndicator
import com.medTech.Douglas.domain.entity.FallRiskAssessment
import com.medTech.Douglas.domain.entity.HandHygieneAssessment
import com.medTech.Douglas.domain.entity.PressureInjuryRiskAssessment
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IndicatorMapper {

    fun toDomain(request: ComplianceIndicatorRequest): ComplianceIndicator {
        return ComplianceIndicator(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            completeWristband = request.completeWristband,
            patientCommunication = request.patientCommunication,
            medicationIdentified = request.medicationIdentified,
            handHygieneAdherence = request.handHygieneAdherence,
            fallRiskAssessment = request.fallRiskAssessment,
            pressureInjuryRiskAssessment = request.pressureInjuryRiskAssessment,
            totalPatients = request.totalPatients,
            observations = request.observations
        )
    }

    fun toResponse(domain: ComplianceIndicator): ComplianceIndicatorResponse {
        return ComplianceIndicatorResponse(
            id = domain.id.toString(),
            periodId = domain.periodId.toString(),
            sectorId = domain.sectorId.toString(),
            completeWristband = domain.completeWristband,
            patientCommunication = domain.patientCommunication,
            medicationIdentified = domain.medicationIdentified,
            handHygieneAdherence = domain.handHygieneAdherence,
            fallRiskAssessment = domain.fallRiskAssessment,
            pressureInjuryRiskAssessment = domain.pressureInjuryRiskAssessment,
            totalPatients = domain.totalPatients,
            observations = domain.observations,
            createdAt = domain.createdAt.toString()
        )
    }

    fun toDomain(request: HandHygieneRequest): HandHygieneAssessment {
        return HandHygieneAssessment.create(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            totalObservations = request.totalObservations,
            compliantObservations = request.compliantObservations
        )
    }

    fun toResponse(domain: HandHygieneAssessment): HandHygieneResponse {
        return HandHygieneResponse(
            id = domain.id.toString(),
            periodId = domain.periodId.toString(),
            sectorId = domain.sectorId.toString(),
            totalObservations = domain.totalObservations,
            compliantObservations = domain.compliantObservations,
            compliancePercentage = domain.compliancePercentage,
            createdAt = domain.createdAt.toString()
        )
    }

    fun toDomain(request: FallRiskRequest): FallRiskAssessment {
        return FallRiskAssessment.create(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            totalPatients = request.totalPatients,
            assessedOnAdmission = request.assessedOnAdmission,
            highRisk = request.highRisk,
            mediumRisk = request.mediumRisk,
            lowRisk = request.lowRisk,
            notAssessed = request.notAssessed
        )
    }

    fun toResponse(domain: FallRiskAssessment): FallRiskResponse {
        return FallRiskResponse(
            id = domain.id.toString(),
            periodId = domain.periodId.toString(),
            sectorId = domain.sectorId.toString(),
            totalPatients = domain.totalPatients,
            assessedOnAdmission = domain.assessedOnAdmission,
            assessmentPercentage = domain.assessmentPercentage,
            highRisk = domain.highRisk,
            mediumRisk = domain.mediumRisk,
            lowRisk = domain.lowRisk,
            notAssessed = domain.notAssessed,
            highRiskPercentage = domain.highRiskPercentage,
            mediumRiskPercentage = domain.mediumRiskPercentage,
            lowRiskPercentage = domain.lowRiskPercentage,
            notAssessedPercentage = domain.notAssessedPercentage,
            createdAt = domain.createdAt.toString()
        )
    }

    fun toDomain(request: PressureInjuryRiskRequest): PressureInjuryRiskAssessment {
        return PressureInjuryRiskAssessment.create(
            periodId = UUID.fromString(request.periodId),
            sectorId = UUID.fromString(request.sectorId),
            totalPatients = request.totalPatients,
            assessedOnAdmission = request.assessedOnAdmission,
            highRisk = request.highRisk,
            mediumRisk = request.mediumRisk,
            lowRisk = request.lowRisk,
            notAssessed = request.notAssessed
        )
    }

    fun toResponse(domain: PressureInjuryRiskAssessment): PressureInjuryRiskResponse {
        return PressureInjuryRiskResponse(
            id = domain.id.toString(),
            periodId = domain.periodId.toString(),
            sectorId = domain.sectorId.toString(),
            totalPatients = domain.totalPatients,
            assessedOnAdmission = domain.assessedOnAdmission,
            assessmentPercentage = domain.assessmentPercentage,
            highRisk = domain.highRisk,
            mediumRisk = domain.mediumRisk,
            lowRisk = domain.lowRisk,
            notAssessed = domain.notAssessed,
            highRiskPercentage = domain.highRiskPercentage,
            mediumRiskPercentage = domain.mediumRiskPercentage,
            lowRiskPercentage = domain.lowRiskPercentage,
            notAssessedPercentage = domain.notAssessedPercentage,
            createdAt = domain.createdAt.toString()
        )
    }
}
