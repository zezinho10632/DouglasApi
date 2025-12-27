package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.professionalcategory.CreateProfessionalCategoryRequest
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import com.medTech.Douglas.api.dto.professionalcategory.UpdateProfessionalCategoryRequest
import com.medTech.Douglas.service.ProfessionalCategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/professional-categories")
@Tag(name = "Categorias Profissionais", description = "Gestão de Categorias Profissionais (para Ranking)")
class ProfessionalCategoryController(
    private val service: ProfessionalCategoryService
) {

    @GetMapping
    @Operation(summary = "Listar todas as categorias profissionais")
    fun listAll(@RequestParam(defaultValue = "true") activeOnly: Boolean): ResponseEntity<ApiResponse<List<ProfessionalCategoryResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria profissional (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateProfessionalCategoryRequest): ResponseEntity<ApiResponse<ProfessionalCategoryResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria profissional (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateProfessionalCategoryRequest
    ): ResponseEntity<ApiResponse<ProfessionalCategoryResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria profissional (Admin/Manager)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
