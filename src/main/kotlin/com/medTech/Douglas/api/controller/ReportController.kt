package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.report.CompletePanelReportResponse
import com.medTech.Douglas.service.usecase.report.GenerateCompletePanelReportUseCase
import com.medTech.Douglas.service.usecase.report.GeneratePanelReportByRangeUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Relatórios", description = "APIs de Geração de Relatórios")
class ReportController(
    private val generateCompletePanelReportUseCase: GenerateCompletePanelReportUseCase,
    private val generatePanelReportByRangeUseCase: GeneratePanelReportByRangeUseCase
) {

    @GetMapping("/panel")
    @Operation(summary = "Gerar relatório completo do painel (Mensal)")
    @PreAuthorize("hasRole('ADMIN')")
    fun generateCompletePanelReport(
        @RequestParam periodId: UUID,
        @RequestParam sectorId: UUID,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<ApiResponse<CompletePanelReportResponse>> {
        val response = generateCompletePanelReportUseCase.execute(periodId, sectorId, startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/panel/range")
    @Operation(summary = "Gerar relatório completo do painel por intervalo (Trimestral/Semestral/Anual)")
    @PreAuthorize("hasRole('ADMIN')")
    fun generatePanelReportByRange(
        @RequestParam sectorId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<ApiResponse<List<CompletePanelReportResponse>>> {
        val response = generatePanelReportByRangeUseCase.execute(sectorId, startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
