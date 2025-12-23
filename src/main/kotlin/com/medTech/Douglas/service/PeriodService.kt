package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.period.CreatePeriodRequest
import com.medTech.Douglas.api.dto.period.PeriodResponse
import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.BusinessRuleException
import com.medTech.Douglas.exception.PeriodNotFoundException
import com.medTech.Douglas.repository.PeriodRepository
import com.medTech.Douglas.service.mapper.PeriodMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PeriodService(
    private val repository: PeriodRepository,
    private val mapper: PeriodMapper
) {

    @Transactional
    fun create(request: CreatePeriodRequest): PeriodResponse {
        val sectorId = UUID.fromString(request.sectorId)
        
        // Check if there is already an open period for this sector
        if (repository.findBySectorIdAndStatus(sectorId, PeriodStatus.OPEN).isPresent) {
            throw BusinessRuleException("There is already an open period for this sector")
        }

        // Check if period for this month/year already exists
        if (repository.existsBySectorIdAndMonthAndYear(sectorId, request.month, request.year)) {
             throw BusinessRuleException("Period for this month and year already exists")
        }

        val period = mapper.toDomain(request)
        val savedPeriod = repository.save(period)
        return mapper.toResponse(savedPeriod)
    }

    @Transactional
    fun close(id: UUID): PeriodResponse {
        val period = repository.findById(id)
            .orElseThrow { PeriodNotFoundException("Period with id $id not found") }

        period.close()
        
        val savedPeriod = repository.save(period)
        return mapper.toResponse(savedPeriod)
    }

    @Transactional
    fun reopen(id: UUID): PeriodResponse {
        val period = repository.findById(id)
            .orElseThrow { PeriodNotFoundException("Period with id $id not found") }

        // Check if there is already an open period for this sector
        if (repository.findBySectorIdAndStatus(period.sectorId, PeriodStatus.OPEN).isPresent) {
            throw BusinessRuleException("There is already an open period for this sector")
        }

        period.reopen()
        
        val savedPeriod = repository.save(period)
        return mapper.toResponse(savedPeriod)
    }

    @Transactional(readOnly = true)
    fun listBySector(sectorId: UUID): List<PeriodResponse> {
        return repository.findBySectorId(sectorId)
            .map { mapper.toResponse(it) }
    }
}
