package com.medTech.Douglas.api.dto.notification

import java.util.UUID

data class UpdateNotificationRequest(
    val classificationId: UUID,
    val categoryId: UUID,
    val professionalCategoryId: UUID?,
    val isSelfNotification: Boolean,
    val quantity: Int
)
