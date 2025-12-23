package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.sector.CreateSectorRequest
import com.medTech.Douglas.api.dto.sector.SectorResponse
import com.medTech.Douglas.api.dto.sector.UpdateSectorRequest
import com.medTech.Douglas.exception.SectorNotFoundException
import com.medTech.Douglas.exception.BusinessRuleException
import com.medTech.Douglas.repository.SectorRepository
import com.medTech.Douglas.service.mapper.SectorMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SectorService(
    private val repository: SectorRepository,
    private val mapper: SectorMapper
) {

    @Transactional
    fun create(request: CreateSectorRequest): SectorResponse {
        if (repository.existsByCode(request.code)) {
            throw BusinessRuleException("Sector with code ${request.code} already exists")
        }

        val sector = mapper.toDomain(request)
        val savedSector = repository.save(sector)
        return mapper.toResponse(savedSector)
    }

    @Transactional
    fun update(id: UUID, request: UpdateSectorRequest): SectorResponse {
        val sector = repository.findById(id)
            .orElseThrow { SectorNotFoundException("Sector with id $id not found") }

        sector.update(request.name)
        
        val savedSector = repository.save(sector)
        return mapper.toResponse(savedSector)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repository.existsById(id)) {
            throw SectorNotFoundException("Sector with id $id not found")
        }
        repository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): SectorResponse {
        val sector = repository.findById(id)
            .orElseThrow { SectorNotFoundException("Sector with id $id not found") }
        
        return mapper.toResponse(sector)
    }

    @Transactional(readOnly = true)
    fun listActive(): List<SectorResponse> {
        return repository.findAllByActiveTrue()
            .map { mapper.toResponse(it) }
    }
}
