package com.medTech.Douglas.service.usecase.notification

import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.repository.NotificationRepository
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.mapper.NotificationMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ListNotificationsByDateRangeUseCase(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
    private val mapper: NotificationMapper
) {
    @Transactional(readOnly = true)
    fun execute(startDate: LocalDate, endDate: LocalDate): List<NotificationResponse> {
        val notifications = repository.findByNotificationDateBetween(startDate, endDate)
        val userIds = notifications.mapNotNull { it.createdBy }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { it.id }

        return notifications.map { mapper.toResponse(it, users) }
    }
}
