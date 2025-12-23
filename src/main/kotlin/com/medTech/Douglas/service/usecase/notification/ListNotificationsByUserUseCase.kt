package com.medTech.Douglas.service.usecase.notification

import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.repository.NotificationRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.NotificationMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class ListNotificationsByUserUseCase(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
    private val mapper: NotificationMapper
) {
    @Transactional(readOnly = true)
    fun execute(userId: UUID): List<NotificationResponse> {
        val user = userRepository.findById(userId).orElse(null)
        return repository.findByCreatedBy(userId)
            .map { mapper.toResponse(it, user) }
    }
}
