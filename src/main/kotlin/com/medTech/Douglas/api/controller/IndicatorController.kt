package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.indicator.*
import com.medTech.Douglas.service.IndicatorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/indicators")
@Tag(name = "Indicadores", description = "APIs de Gestão de Indicadores")
class IndicatorController(
    private val indicatorService: IndicatorService
) {

    // Compliance
    @PostMapping("/compliance")
    @Operation(summary = "Salvar indicador de conformidade")
    fun saveCompliance(@RequestBody request: ComplianceIndicatorRequest): ResponseEntity<ApiResponse<ComplianceIndicatorResponse>> {
        val response = indicatorService.saveCompliance(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Indicador de conformidade salvo com sucesso"))
    }

    @PutMapping("/compliance/{id}")
    @Operation(summary = "Atualizar indicador de conformidade")
    fun updateCompliance(@PathVariable id: UUID, @RequestBody request: ComplianceIndicatorRequest): ResponseEntity<ApiResponse<ComplianceIndicatorResponse>> {
        val response = indicatorService.updateCompliance(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Indicador de conformidade atualizado com sucesso"))
    }

    @GetMapping("/compliance/period/{periodId}")
    @Operation(summary = "Buscar indicador de conformidade por período")
    fun getCompliance(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<ComplianceIndicatorResponse?>> {
        val response = indicatorService.getComplianceByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/compliance/{id}")
    @Operation(summary = "Buscar indicador de conformidade por ID")
    fun getComplianceById(@PathVariable id: UUID): ResponseEntity<ApiResponse<ComplianceIndicatorResponse?>> {
        val response = indicatorService.getComplianceById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    // Hand Hygiene
    @PostMapping("/hand-hygiene")
    @Operation(summary = "Salvar avaliação de higiene das mãos")
    fun saveHandHygiene(@RequestBody request: HandHygieneRequest): ResponseEntity<ApiResponse<HandHygieneResponse>> {
        val response = indicatorService.saveHandHygiene(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de higiene das mãos salva com sucesso"))
    }

    @PutMapping("/hand-hygiene/{id}")
    @Operation(summary = "Atualizar avaliação de higiene das mãos")
    fun updateHandHygiene(@PathVariable id: UUID, @RequestBody request: HandHygieneRequest): ResponseEntity<ApiResponse<HandHygieneResponse>> {
        val response = indicatorService.updateHandHygiene(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de higiene das mãos atualizada com sucesso"))
    }

    @GetMapping("/hand-hygiene/period/{periodId}")
    @Operation(summary = "Buscar avaliação de higiene das mãos por período")
    fun getHandHygiene(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<HandHygieneResponse?>> {
        val response = indicatorService.getHandHygieneByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/hand-hygiene/{id}")
    @Operation(summary = "Buscar avaliação de higiene das mãos por ID")
    fun getHandHygieneById(@PathVariable id: UUID): ResponseEntity<ApiResponse<HandHygieneResponse?>> {
        val response = indicatorService.getHandHygieneById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    // Fall Risk
    @PostMapping("/fall-risk")
    @Operation(summary = "Salvar avaliação de risco de queda")
    fun saveFallRisk(@RequestBody request: FallRiskRequest): ResponseEntity<ApiResponse<FallRiskResponse>> {
        val response = indicatorService.saveFallRisk(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de risco de queda salva com sucesso"))
    }

    @PutMapping("/fall-risk/{id}")
    @Operation(summary = "Atualizar avaliação de risco de queda")
    fun updateFallRisk(@PathVariable id: UUID, @RequestBody request: FallRiskRequest): ResponseEntity<ApiResponse<FallRiskResponse>> {
        val response = indicatorService.updateFallRisk(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de risco de queda atualizada com sucesso"))
    }

    @GetMapping("/fall-risk/period/{periodId}")
    @Operation(summary = "Buscar avaliação de risco de queda por período")
    fun getFallRisk(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<FallRiskResponse?>> {
        val response = indicatorService.getFallRiskByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/fall-risk/{id}")
    @Operation(summary = "Buscar avaliação de risco de queda por ID")
    fun getFallRiskById(@PathVariable id: UUID): ResponseEntity<ApiResponse<FallRiskResponse?>> {
        val response = indicatorService.getFallRiskById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    // Pressure Injury Risk
    @PostMapping("/pressure-injury")
    @Operation(summary = "Salvar avaliação de risco de lesão por pressão")
    fun savePressureInjury(@RequestBody request: PressureInjuryRiskRequest): ResponseEntity<ApiResponse<PressureInjuryRiskResponse>> {
        val response = indicatorService.savePressureInjuryRisk(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de risco de lesão por pressão salva com sucesso"))
    }

    @PutMapping("/pressure-injury/{id}")
    @Operation(summary = "Atualizar avaliação de risco de lesão por pressão")
    fun updatePressureInjury(@PathVariable id: UUID, @RequestBody request: PressureInjuryRiskRequest): ResponseEntity<ApiResponse<PressureInjuryRiskResponse>> {
        val response = indicatorService.updatePressureInjuryRisk(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de risco de lesão por pressão atualizada com sucesso"))
    }

    @GetMapping("/pressure-injury/period/{periodId}")
    @Operation(summary = "Buscar avaliação de risco de lesão por pressão por período")
    fun getPressureInjury(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<PressureInjuryRiskResponse?>> {
        val response = indicatorService.getPressureInjuryRiskByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/pressure-injury/{id}")
    @Operation(summary = "Buscar avaliação de risco de lesão por pressão por ID")
    fun getPressureInjuryById(@PathVariable id: UUID): ResponseEntity<ApiResponse<PressureInjuryRiskResponse?>> {
        val response = indicatorService.getPressureInjuryRiskById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
