package com.medTech.Douglas.service.usecase.adverseevent

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.repository.AdverseEventRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.AdverseEventMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class ListAdverseEventsByUserUseCase(
    private val repository: AdverseEventRepository,
    private val userRepository: UserRepository,
    private val mapper: AdverseEventMapper
) {
    @Transactional(readOnly = true)
    fun execute(userId: UUID): List<AdverseEventResponse> {
        val user = userRepository.findById(userId).orElse(null)
        return repository.findByCreatedBy(userId)
            .map { mapper.toResponse(it, user) }
    }
}
