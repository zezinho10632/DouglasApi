package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.classification.ClassificationResponse
import com.medTech.Douglas.api.dto.classification.CreateClassificationRequest
import com.medTech.Douglas.api.dto.classification.UpdateClassificationRequest
import com.medTech.Douglas.service.NotificationClassificationService
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
@RequestMapping("/api/v1/classifications")
@Tag(name = "Classificações", description = "Gestão de Classificações de Notificação")
class NotificationClassificationController(
    private val service: NotificationClassificationService
) {

    @GetMapping
    @Operation(summary = "Listar todas as classificações", description = "Lista todas as classificações cadastradas, com opção de filtrar apenas as ativas.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ])
    fun listAll(
        @Parameter(description = "Filtrar apenas ativos (padrão: true)")
        @RequestParam(defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<ApiResponse<List<ClassificationResponse>>> {
        val response = if (activeOnly) service.listActive() else service.listAll()
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping
    @Operation(summary = "Criar nova classificação (Admin/Manager)", description = "Cria uma nova classificação de notificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Classificação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Classificação já existe")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateClassificationRequest): ResponseEntity<ApiResponse<ClassificationResponse>> {
        val response = service.create(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar classificação (Admin/Manager)", description = "Atualiza o nome ou status de uma classificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Classificação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Classificação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Nome já utilizado por outra classificação")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @Parameter(description = "ID da classificação", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateClassificationRequest
    ): ResponseEntity<ApiResponse<ClassificationResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar classificação (Admin/Manager)", description = "Remove (soft delete) uma classificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Classificação deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Classificação não encontrada")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(
        @Parameter(description = "ID da classificação", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
