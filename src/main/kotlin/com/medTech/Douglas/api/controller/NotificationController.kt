package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.notification.UpdateNotificationRequest
import com.medTech.Douglas.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificações", description = "APIs de Gestão de Notificações")
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    @Operation(summary = "Criar uma nova notificação", description = "Cria uma nova notificação de incidente ou evento para um período e setor.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Notificação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    fun create(@RequestBody request: CreateNotificationRequest): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Notificação criada com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma notificação existente", description = "Atualiza os dados de uma notificação existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Notificação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Notificação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @Parameter(description = "ID da notificação", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateNotificationRequest
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Notificação atualizada com sucesso"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID", description = "Retorna os detalhes de uma notificação específica.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Notificação encontrada"),
        SwaggerApiResponse(responseCode = "404", description = "Notificação não encontrada")
    ])
    fun findById(
        @Parameter(description = "ID da notificação", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    @Operation(summary = "Listar notificações por período com filtros opcionais", description = "Lista notificações filtradas por período, classificação e categoria.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ])
    @PreAuthorize("hasRole('ADMIN')")
    fun listByPeriod(
        @Parameter(description = "ID do período", required = true)
        @RequestParam periodId: UUID,

        @Parameter(description = "ID da Classificação", required = false)
        @RequestParam(required = false) classificationId: UUID?,

        @Parameter(description = "ID da Categoria", required = false)
        @RequestParam(required = false) categoryId: UUID?
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val response = notificationService.listByPeriod(periodId, classificationId, categoryId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
