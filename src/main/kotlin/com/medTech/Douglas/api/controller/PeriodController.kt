package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.period.CreatePeriodRequest
import com.medTech.Douglas.api.dto.period.PeriodResponse
import com.medTech.Douglas.service.PeriodService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/periods")
@Tag(name = "Períodos", description = "APIs de Gestão de Períodos")
class PeriodController(
    private val periodService: PeriodService
) {

    @PostMapping
    @Operation(summary = "Abrir um novo período")
    fun create(@RequestBody request: CreatePeriodRequest): ResponseEntity<ApiResponse<PeriodResponse>> {
        val response = periodService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Período aberto com sucesso"))
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Fechar um período")
    fun close(@PathVariable id: UUID): ResponseEntity<ApiResponse<PeriodResponse>> {
        val response = periodService.close(id)
        return ResponseEntity.ok(ApiResponse.success(response, "Período fechado com sucesso"))
    }

    @PostMapping("/{id}/reopen")
    @Operation(summary = "Reabrir um período")
    fun reopen(@PathVariable id: UUID): ResponseEntity<ApiResponse<PeriodResponse>> {
        val response = periodService.reopen(id)
        return ResponseEntity.ok(ApiResponse.success(response, "Período reaberto com sucesso"))
    }

    @GetMapping
    @Operation(summary = "Listar períodos por setor")
    fun listBySector(@RequestParam sectorId: UUID): ResponseEntity<ApiResponse<List<PeriodResponse>>> {
        val response = periodService.listBySector(sectorId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
