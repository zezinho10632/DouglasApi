package com.medTech.Douglas.api.dto.adverseevent

import com.medTech.Douglas.domain.enums.JobTitle
import java.time.LocalDate
import java.time.LocalDateTime

data class AdverseEventResponse(
    val id: String,
    val periodId: String,
    val sectorId: String,
    val eventDate: LocalDate,
    val eventType: String,
    val description: String,
    val quantityCases: Int,
    val quantityNotifications: Int,
    val createdByName: String? = null,
    val createdByJobTitle: JobTitle? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
