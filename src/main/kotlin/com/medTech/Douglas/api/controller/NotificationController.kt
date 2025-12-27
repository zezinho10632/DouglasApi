package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.notification.UpdateNotificationRequest
import com.medTech.Douglas.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    @Operation(summary = "Criar uma nova notificação")
    fun create(@RequestBody request: CreateNotificationRequest): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Notificação criada com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma notificação existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateNotificationRequest
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Notificação atualizada com sucesso"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    @Operation(summary = "Listar notificações por período com filtros opcionais")
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
