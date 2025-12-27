package com.medTech.Douglas.api.dto.category

import java.util.UUID

data class CreateCategoryRequest(val name: String)
data class UpdateCategoryRequest(val name: String, val active: Boolean?)
data class CategoryResponse(val id: UUID, val name: String, val active: Boolean)
