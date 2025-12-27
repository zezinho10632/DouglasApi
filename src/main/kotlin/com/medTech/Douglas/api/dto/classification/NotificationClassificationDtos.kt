package com.medTech.Douglas.api.dto.classification

import java.util.UUID

data class CreateClassificationRequest(val name: String)
data class UpdateClassificationRequest(val name: String, val active: Boolean?)
data class ClassificationResponse(val id: UUID, val name: String, val active: Boolean)
