package com.medTech.Douglas.api.dto.notification

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.NotificationClassification
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val periodId: UUID,
    val sectorId: UUID,
    val notificationDate: LocalDate,
    val classification: NotificationClassification,
    val category: String,
    val subcategory: String,
    val description: String,
    val isSelfNotification: Boolean,
    val professionalCategory: String,
    val professionalName: String?,
    val createdByName: String? = null,
    val createdByJobTitle: JobTitle? = null,
    val createdAt: LocalDateTime
)
