package com.medTech.Douglas.service.usecase.adverseevent

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.repository.AdverseEventRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.AdverseEventMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ListAdverseEventsByDateRangeUseCase(
    private val repository: AdverseEventRepository,
    private val userRepository: UserRepository,
    private val mapper: AdverseEventMapper
) {
    @Transactional(readOnly = true)
    fun execute(startDate: LocalDate, endDate: LocalDate): List<AdverseEventResponse> {
        val events = repository.findByEventDateBetween(startDate, endDate)
        val userIds = events.mapNotNull { it.createdBy }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { it.id }
        
        return events.map { mapper.toResponse(it, users) }
    }
}
