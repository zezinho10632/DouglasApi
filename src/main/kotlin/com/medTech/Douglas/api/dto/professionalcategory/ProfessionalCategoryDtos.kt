package com.medTech.Douglas.api.dto.professionalcategory

import java.util.UUID

data class CreateProfessionalCategoryRequest(val name: String)
data class UpdateProfessionalCategoryRequest(val name: String, val active: Boolean?)
data class ProfessionalCategoryResponse(val id: UUID, val name: String, val active: Boolean)
