package com.medTech.Douglas.api.dto.notification

import com.medTech.Douglas.domain.enums.NotificationClassification
import java.time.LocalDate

data class UpdateNotificationRequest(
    val notificationDate: LocalDate,
    val classification: NotificationClassification,
    val category: String,
    val subcategory: String,
    val description: String,
    val isSelfNotification: Boolean,
    val professionalCategory: String,
    val professionalName: String?
)
