package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.domain.entity.AuditLog
import com.medTech.Douglas.service.AuditLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Logs de Auditoria", description = "APIs de Consulta de Logs de Auditoria")
class AuditLogController(
    private val auditLogService: AuditLogService
) {

    @GetMapping
    @Operation(summary = "Consultar logs de auditoria com filtros", description = "Permite filtrar logs por ação, recurso, email de usuário e intervalo de datas. Todos os filtros são opcionais.")
    @PreAuthorize("hasRole('ADMIN')")
    fun search(
        @Parameter(description = "Ação realizada (ex: CREATE, UPDATE, DELETE)", required = false)
        @RequestParam(required = false) action: String?,

        @Parameter(description = "Recurso afetado (ex: Notification, Period)", required = false)
        @RequestParam(required = false) resource: String?,

        @Parameter(description = "Email do usuário que realizou a ação (busca parcial)", required = false)
        @RequestParam(required = false) userEmail: String?,

        @Parameter(description = "Data de início para filtro (formato ISO Date Time)", required = false)
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,

        @Parameter(description = "Data de fim para filtro (formato ISO Date Time)", required = false)
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): ResponseEntity<ApiResponse<List<AuditLog>>> {
        val logs = auditLogService.search(action, resource, userEmail, startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(logs))
    }
}
