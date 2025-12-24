package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.notification.CreateNotificationRequest
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.notification.UpdateNotificationRequest
import com.medTech.Douglas.service.NotificationService
import com.medTech.Douglas.service.usecase.notification.ListNotificationsByDateRangeUseCase
import com.medTech.Douglas.service.usecase.notification.ListNotificationsByUserUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

import com.medTech.Douglas.domain.enums.NotificationClassification

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificações", description = "APIs de Gestão de Notificações")
class NotificationController(
    private val notificationService: NotificationService,
    private val listNotificationsByDateRangeUseCase: ListNotificationsByDateRangeUseCase,
    private val listNotificationsByUserUseCase: ListNotificationsByUserUseCase
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma notificação")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        notificationService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Notificação excluída com sucesso"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<NotificationResponse>> {
        val response = notificationService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    @Operation(summary = "Listar notificações por período com filtros opcionais", description = "Lista notificações de um período, podendo filtrar por classificação e categoria.")
    @PreAuthorize("hasRole('ADMIN')")
    fun listByPeriod(
        @Parameter(description = "ID do período", required = true)
        @RequestParam periodId: UUID,

        @Parameter(description = "Classificação da notificação (ex: INCIDENT, NEAR_MISS)", required = false)
        @RequestParam(required = false) classification: NotificationClassification?,

        @Parameter(description = "Categoria da notificação (busca parcial)", required = false)
        @RequestParam(required = false) category: String?
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val response = notificationService.listByPeriod(periodId, classification, category)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/range")
    @Operation(summary = "Listar notificações por intervalo de datas")
    fun listByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val response = listNotificationsByDateRangeUseCase.execute(startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar notificações criadas por um usuário")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    fun listByUser(@PathVariable userId: UUID): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val response = listNotificationsByUserUseCase.execute(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
