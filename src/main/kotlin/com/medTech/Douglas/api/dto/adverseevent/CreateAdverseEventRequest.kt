package com.medTech.Douglas.api.dto.adverseevent

import java.time.LocalDate

data class CreateAdverseEventRequest(
    val periodId: String,
    val sectorId: String,
    val eventDate: LocalDate,
    val eventType: String,
    val description: String,
    val quantityCases: Int,
    val quantityNotifications: Int
)
