package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.classification.CreateClassificationRequest
import com.medTech.Douglas.api.dto.classification.UpdateClassificationRequest
import com.medTech.Douglas.service.NotificationClassificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/classifications")
@Tag(name = "Classificações", description = "Gestão de Classificações de Notificação")
class NotificationClassificationController(
    private val service: NotificationClassificationService
) {

    @GetMapping
    @Operation(summary = "Listar todas as classificações")
    fun listAll(@RequestParam(defaultValue = "true") activeOnly: Boolean): ResponseEntity<ApiResponse<List<ClassificationResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova classificação (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateClassificationRequest): ResponseEntity<ApiResponse<ClassificationResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar classificação (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateClassificationRequest
    ): ResponseEntity<ApiResponse<ClassificationResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar classificação (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
