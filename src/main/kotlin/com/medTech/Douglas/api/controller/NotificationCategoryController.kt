package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.category.CategoryResponse
import com.medTech.Douglas.api.dto.category.CreateCategoryRequest
import com.medTech.Douglas.api.dto.category.UpdateCategoryRequest
import com.medTech.Douglas.service.NotificationCategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categorias", description = "Gestão de Categorias de Notificação")
class NotificationCategoryController(
    private val service: NotificationCategoryService
) {

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    fun listAll(@RequestParam(defaultValue = "true") activeOnly: Boolean): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateCategoryRequest): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
