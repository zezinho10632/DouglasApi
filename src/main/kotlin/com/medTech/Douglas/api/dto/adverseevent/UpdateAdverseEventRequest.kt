package com.medTech.Douglas.api.dto.adverseevent

import java.time.LocalDate

data class UpdateAdverseEventRequest(
    val eventDate: LocalDate,
    val eventType: String,
    val description: String,
    val quantityCases: Int,
    val quantityNotifications: Int
)
