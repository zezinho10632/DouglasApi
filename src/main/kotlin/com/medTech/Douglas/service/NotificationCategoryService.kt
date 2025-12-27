package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.category.CategoryResponse
import com.medTech.Douglas.api.dto.category.CreateCategoryRequest
import com.medTech.Douglas.api.dto.category.UpdateCategoryRequest
import com.medTech.Douglas.domain.entity.NotificationCategory
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.NotificationCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NotificationCategoryService(
    private val repository: NotificationCategoryRepository
) {
    @Transactional(readOnly = true)
    fun listAll(): List<CategoryResponse> {
        return repository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun listActive(): List<CategoryResponse> {
        return repository.findAllByActiveTrue().map { toResponse(it) }
    }

    @Transactional
    fun create(request: CreateCategoryRequest): CategoryResponse {
        val entity = NotificationCategory(name = request.name)
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun update(id: UUID, request: UpdateCategoryRequest): CategoryResponse {
        val entity = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }
        
        entity.name = request.name
        request.active?.let { entity.active = it }
        
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException("Category not found with id: $id")
        }
        repository.deleteById(id)
    }

    private fun toResponse(entity: NotificationCategory) = CategoryResponse(
        id = entity.id,
        name = entity.name,
        active = entity.active
    )
}
