package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.professionalcategory.CreateProfessionalCategoryRequest
import com.medTech.Douglas.api.dto.professionalcategory.ProfessionalCategoryResponse
import com.medTech.Douglas.api.dto.professionalcategory.UpdateProfessionalCategoryRequest
import com.medTech.Douglas.service.ProfessionalCategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
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
    @Operation(summary = "Listar todas as categorias profissionais", description = "Lista todas as categorias profissionais cadastradas, com opção de filtrar apenas as ativas.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ])
    fun listAll(
        @Parameter(description = "Filtrar apenas ativos (padrão: true)")
        @RequestParam(defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<ApiResponse<List<ProfessionalCategoryResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria profissional (Admin/Manager)", description = "Cria uma nova categoria profissional. O nome deve ser único.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Categoria criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Categoria já existe")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateProfessionalCategoryRequest): ResponseEntity<ApiResponse<ProfessionalCategoryResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria profissional (Admin/Manager)", description = "Atualiza o nome ou status de uma categoria profissional.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Nome já utilizado por outra categoria")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @Parameter(description = "ID da categoria", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateProfessionalCategoryRequest
    ): ResponseEntity<ApiResponse<ProfessionalCategoryResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria profissional (Admin/Manager)", description = "Remove (soft delete) uma categoria profissional.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Categoria deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Categoria não encontrada")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(
        @Parameter(description = "ID da categoria", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
