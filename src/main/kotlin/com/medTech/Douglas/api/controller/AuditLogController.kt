package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.domain.entity.AuditLog
import com.medTech.Douglas.service.AuditLogService
import io.swagger.v3.oas.annotations.Operation
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
    @Operation(summary = "Consultar logs de auditoria com filtros")
    @PreAuthorize("hasRole('ADMIN')")
    fun search(
        @RequestParam(required = false) action: String?,
        @RequestParam(required = false) resource: String?,
        @RequestParam(required = false) userEmail: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): ResponseEntity<ApiResponse<List<AuditLog>>> {
        val logs = auditLogService.search(action, resource, userEmail, startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(logs))
    }
}
