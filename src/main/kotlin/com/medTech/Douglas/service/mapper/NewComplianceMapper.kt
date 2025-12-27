package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.compliance.*
import com.medTech.Douglas.domain.entity.MedicationCompliance
import com.medTech.Douglas.domain.entity.MetaCompliance
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NewComplianceMapper {

    fun toDomain(request: CreateMetaComplianceRequest, userId: UUID?): MetaCompliance {
        return MetaCompliance(
            periodId = request.periodId,
            sectorId = request.sectorId,
            goalValue = request.goalValue,
            percentage = request.percentage,
            createdBy = userId
        )
    }

    fun toResponse(domain: MetaCompliance): MetaComplianceResponse {
        return MetaComplianceResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            goalValue = domain.goalValue,
            percentage = domain.percentage,
            createdBy = domain.createdBy
        )
    }

    fun toDomain(request: CreateMedicationComplianceRequest, userId: UUID?): MedicationCompliance {
        return MedicationCompliance(
            periodId = request.periodId,
            sectorId = request.sectorId,
            percentage = request.percentage,
            createdBy = userId
        )
    }

    fun toResponse(domain: MedicationCompliance): MedicationComplianceResponse {
        return MedicationComplianceResponse(
            id = domain.id,
            periodId = domain.periodId,
            sectorId = domain.sectorId,
            percentage = domain.percentage,
            createdBy = domain.createdBy
        )
    }
}
