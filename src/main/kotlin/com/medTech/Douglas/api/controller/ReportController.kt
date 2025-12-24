package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.report.CompletePanelReportResponse
import com.medTech.Douglas.domain.enums.ReportPeriodicity
import com.medTech.Douglas.service.usecase.report.GenerateCompletePanelReportUseCase
import com.medTech.Douglas.service.usecase.report.GenerateCumulativePanelReportUseCase
import com.medTech.Douglas.service.usecase.report.GeneratePanelReportByRangeUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    private val generatePanelReportByRangeUseCase: GeneratePanelReportByRangeUseCase,
    private val generateCumulativePanelReportUseCase: GenerateCumulativePanelReportUseCase
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

    @GetMapping("/panel/cumulative")
    @Operation(
        summary = "Gerar relatório acumulativo do painel",
        description = "Gera um relatório único consolidando dados de um intervalo. Pode usar periodicidade pré-definida (MENSAL, TRIMESTRAL, etc.) ou datas customizadas."
    )
    @PreAuthorize("hasRole('ADMIN')")
    fun generateCumulativePanelReport(
        @Parameter(description = "ID do setor", required = true)
        @RequestParam sectorId: UUID,

        @Parameter(description = "Periodicidade (CUSTOM, MONTHLY, QUARTERLY, SEMESTRAL, ANNUAL)", required = false)
        @RequestParam(defaultValue = "CUSTOM") periodicity: ReportPeriodicity,

        @Parameter(description = "Ano de referência (necessário para periodicidades pré-definidas)", required = false)
        @RequestParam(required = false) year: Int?,

        @Parameter(description = "Período numérico (Mês 1-12, Trimestre 1-4, Semestre 1-2)", required = false)
        @RequestParam(required = false) period: Int?,

        @Parameter(description = "Data de início (se Periodicidade = CUSTOM)", required = false)
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,

        @Parameter(description = "Data de fim (se Periodicidade = CUSTOM)", required = false)
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<ApiResponse<CompletePanelReportResponse>> {
        val response = generateCumulativePanelReportUseCase.execute(
            sectorId, periodicity, year, period, startDate, endDate
        )
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
