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

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma notificação", description = "Remove permanentemente uma notificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Notificação deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Notificação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun delete(
        @Parameter(description = "ID da notificação", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        notificationService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Notificação deletada com sucesso"))
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
    @Operation(summary = "Listar notificações", description = "Lista notificações filtradas por período (com filtro opcional de classificação) ou por setor.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Parâmetros inválidos (deve fornecer periodId ou sectorId)")
    ])
    @PreAuthorize("hasRole('ADMIN')")
    fun list(
        @Parameter(description = "ID do período (Obrigatório se sectorId não informado)", required = false)
        @RequestParam(required = false) periodId: UUID?,

        @Parameter(description = "ID do setor (Obrigatório se periodId não informado)", required = false)
        @RequestParam(required = false) sectorId: UUID?,

        @Parameter(description = "ID da Classificação (apenas para filtro por período)", required = false)
        @RequestParam(required = false) classificationId: UUID?
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        if (periodId != null) {
            val response = notificationService.listByPeriod(periodId, classificationId)
            return ResponseEntity.ok(ApiResponse.success(response))
        } else if (sectorId != null) {
            val response = notificationService.listBySector(sectorId)
            return ResponseEntity.ok(ApiResponse.success(response))
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Deve fornecer periodId ou sectorId"))
        }
    }

    @GetMapping("/ranking/professional-category")
    @Operation(summary = "Ranking de categorias profissionais", description = "Retorna um ranking de categorias profissionais com base na quantidade de notificações. Pode ser filtrado por período ou setor.")
    @PreAuthorize("hasRole('ADMIN')")
    fun getRanking(
        @Parameter(description = "ID do período", required = false)
        @RequestParam(required = false) periodId: UUID?,

        @Parameter(description = "ID do setor", required = false)
        @RequestParam(required = false) sectorId: UUID?
    ): ResponseEntity<ApiResponse<List<com.medTech.Douglas.api.dto.notification.ProfessionalCategoryRankingResponse>>> {
        val response = notificationService.getProfessionalCategoryRanking(periodId, sectorId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
