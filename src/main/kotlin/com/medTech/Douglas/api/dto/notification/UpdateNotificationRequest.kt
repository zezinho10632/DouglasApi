package com.medTech.Douglas.api.dto.notification

import java.util.UUID

data class UpdateNotificationRequest(
    val classificationId: UUID?,
    val classificationText: String?,
    val description: String,
    val professionalCategoryId: UUID?,
    val professionalCategoryText: String?,
    val quantityClassification: Int,
    val quantityCategory: Int,
    val quantityProfessional: Int,
    val quantity: Int
)
