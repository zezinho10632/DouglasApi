package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.selfnotification.CreateSelfNotificationRequest
import com.medTech.Douglas.api.dto.selfnotification.SelfNotificationResponse
import com.medTech.Douglas.api.dto.selfnotification.UpdateSelfNotificationRequest
import com.medTech.Douglas.service.SelfNotificationService
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
@RequestMapping("/api/v1/self-notifications")
@Tag(name = "Auto-Notificações", description = "APIs de Gestão de Auto-Notificações (Indicador)")
class SelfNotificationController(
    private val service: SelfNotificationService
) {

    @PostMapping
    @Operation(summary = "Criar registro de auto-notificação", description = "Cria um novo registro de auto-notificação para um período e setor.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Auto-Notificação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Auto-Notificação já existe para este período")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun create(@RequestBody request: CreateSelfNotificationRequest): ResponseEntity<ApiResponse<SelfNotificationResponse>> {
        val response = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Auto-Notificação criada com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de auto-notificação", description = "Atualiza os valores de quantidade e porcentagem de uma auto-notificação existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Auto-Notificação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Auto-Notificação não encontrada")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun update(
        @Parameter(description = "ID da auto-notificação", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateSelfNotificationRequest
    ): ResponseEntity<ApiResponse<SelfNotificationResponse>> {
        val response = service.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Auto-Notificação atualizada com sucesso"))
    }

    @GetMapping("/period/{periodId}")
    @Operation(summary = "Buscar auto-notificação por período", description = "Retorna o registro de auto-notificação associado a um período específico.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Auto-Notificação encontrada"),
        SwaggerApiResponse(responseCode = "404", description = "Auto-Notificação não encontrada")
    ])
    fun findByPeriodId(
        @Parameter(description = "ID do período", required = true)
        @PathVariable periodId: UUID
    ): ResponseEntity<ApiResponse<SelfNotificationResponse?>> {
        val response = service.findByPeriodId(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
