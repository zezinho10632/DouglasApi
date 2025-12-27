package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.professionalcategory.CreateProfessionalCategoryRequest
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import com.medTech.Douglas.api.dto.professionalcategory.UpdateProfessionalCategoryRequest
import com.medTech.Douglas.domain.entity.ProfessionalCategory
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.ProfessionalCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProfessionalCategoryService(
    private val repository: ProfessionalCategoryRepository
) {
    @Transactional(readOnly = true)
    fun listAll(): List<ProfessionalCategoryResponse> {
        return repository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun listActive(): List<ProfessionalCategoryResponse> {
        return repository.findAllByActiveTrue().map { toResponse(it) }
    }

    @Transactional
    fun create(request: CreateProfessionalCategoryRequest): ProfessionalCategoryResponse {
        val entity = ProfessionalCategory(name = request.name)
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun update(id: UUID, request: UpdateProfessionalCategoryRequest): ProfessionalCategoryResponse {
        val entity = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Professional Category not found with id: $id") }
        
        entity.name = request.name
        request.active?.let { entity.active = it }
        
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException("Professional Category not found with id: $id")
        }
        repository.deleteById(id)
    }

    private fun toResponse(entity: ProfessionalCategory) = ProfessionalCategoryResponse(
        id = entity.id,
        name = entity.name,
        active = entity.active
    )
}
