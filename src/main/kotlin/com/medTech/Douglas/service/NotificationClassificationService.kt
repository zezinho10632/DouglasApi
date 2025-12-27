package com.medTech.Douglas.service

import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.classification.CreateClassificationRequest
import com.medTech.Douglas.api.dto.classification.UpdateClassificationRequest
import com.medTech.Douglas.domain.entity.NotificationClassification
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.NotificationClassificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NotificationClassificationService(
    private val repository: NotificationClassificationRepository
) {
    @Transactional(readOnly = true)
    fun listAll(): List<ClassificationResponse> {
        return repository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun listActive(): List<ClassificationResponse> {
        return repository.findAllByActiveTrue().map { toResponse(it) }
    }

    @Transactional
    fun create(request: CreateClassificationRequest): ClassificationResponse {
        val entity = NotificationClassification(name = request.name)
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun update(id: UUID, request: UpdateClassificationRequest): ClassificationResponse {
        val entity = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Classification not found with id: $id") }
        
        entity.name = request.name
        request.active?.let { entity.active = it }
        
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException("Classification not found with id: $id")
        }
        repository.deleteById(id)
    }

    private fun toResponse(entity: NotificationClassification) = ClassificationResponse(
        id = entity.id,
        name = entity.name,
        active = entity.active
    )
}
