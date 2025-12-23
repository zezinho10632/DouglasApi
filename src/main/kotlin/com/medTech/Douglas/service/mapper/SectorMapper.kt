package com.medTech.Douglas.service.mapper

import com.medTech.Douglas.api.dto.sector.CreateSectorRequest
import com.medTech.Douglas.api.dto.sector.SectorResponse
import com.medTech.Douglas.domain.entity.Sector
import org.springframework.stereotype.Component

@Component
class SectorMapper {
    fun toDomain(request: CreateSectorRequest): Sector {
        return Sector(
            name = request.name,
            code = request.code
        )
    }

    fun toResponse(domain: Sector): SectorResponse {
        return SectorResponse(
            id = domain.id,
            name = domain.name,
            code = domain.code,
            active = domain.active,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
