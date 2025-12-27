package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.category.CategoryResponse
import com.medTech.Douglas.api.dto.category.CreateCategoryRequest
import com.medTech.Douglas.api.dto.category.UpdateCategoryRequest
import com.medTech.Douglas.service.NotificationCategoryService
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Categorias", description = "Gestão de Categorias de Notificação")
class NotificationCategoryController(
    private val service: NotificationCategoryService
) {

    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Lista todas as categorias de notificação cadastradas, com opção de filtrar apenas as ativas.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ])
    fun listAll(
        @Parameter(description = "Filtrar apenas ativos (padrão: true)")
        @RequestParam(defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria (Admin/Manager)", description = "Cria uma nova categoria de notificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Categoria criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Categoria já existe")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateCategoryRequest): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria (Admin/Manager)", description = "Atualiza o nome ou status de uma categoria de notificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Nome já utilizado por outra categoria")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @Parameter(description = "ID da categoria", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria (Admin/Manager)", description = "Remove (soft delete) uma categoria de notificação.")
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
