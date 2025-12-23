package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.period.CreatePeriodRequest
import com.medTech.Douglas.api.dto.period.PeriodResponse
import com.medTech.Douglas.domain.entity.Period
import com.medTech.Douglas.domain.enums.PeriodStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PeriodMapper {
    fun toDomain(request: CreatePeriodRequest): Period {
        return Period(
            sectorId = UUID.fromString(request.sectorId),
            month = request.month,
            year = request.year
        )
    }

    fun toResponse(domain: Period): PeriodResponse {
        return PeriodResponse(
            id = domain.id,
            sectorId = domain.sectorId,
            month = domain.month,
            year = domain.year,
            active = domain.status == PeriodStatus.OPEN,
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
