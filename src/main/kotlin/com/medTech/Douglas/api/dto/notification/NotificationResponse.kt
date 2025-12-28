package com.medTech.Douglas.api.dto.notification

import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import java.time.LocalDateTime
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val periodId: UUID,
    val sectorId: UUID,
    val classification: ClassificationResponse?,
    val classificationText: String?,
    val description: String,
    val professionalCategory: ProfessionalCategoryResponse?,
    val professionalCategoryText: String?,
    val quantityClassification: Int,
    val quantityCategory: Int,
    val quantityProfessional: Int,
    val quantity: Int,
    val createdBy: UUID?,
    val createdAt: LocalDateTime
)
