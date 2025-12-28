package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.compliance.*
import com.medTech.Douglas.api.dto.indicator.*
import com.medTech.Douglas.service.IndicatorService
import com.medTech.Douglas.service.NewComplianceService
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
@RequestMapping("/api/v1/indicators")
@Tag(name = "Indicadores", description = "APIs de Gestão de Indicadores")
class IndicatorController(
    private val indicatorService: IndicatorService,
    private val newComplianceService: NewComplianceService
) {

    // Compliance
    @PostMapping("/compliance")
    @Operation(summary = "Salvar indicador de conformidade", description = "Cria um novo indicador de conformidade.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Indicador criado com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun saveCompliance(@RequestBody request: ComplianceIndicatorRequest): ResponseEntity<ApiResponse<ComplianceIndicatorResponse>> {
        val response = indicatorService.saveCompliance(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Indicador de conformidade salvo com sucesso"))
    }

    @PutMapping("/compliance/{id}")
    @Operation(summary = "Atualizar indicador de conformidade", description = "Atualiza um indicador de conformidade existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador atualizado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updateCompliance(@PathVariable id: UUID, @RequestBody request: ComplianceIndicatorRequest): ResponseEntity<ApiResponse<ComplianceIndicatorResponse>> {
        val response = indicatorService.updateCompliance(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Indicador de conformidade atualizado com sucesso"))
    }

    @DeleteMapping("/compliance/{id}")
    @Operation(summary = "Deletar indicador de conformidade", description = "Remove permanentemente um indicador de conformidade.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador deletado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deleteCompliance(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        indicatorService.deleteCompliance(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Indicador de conformidade deletado com sucesso"))
    }

    @GetMapping("/compliance/period/{periodId}")
    @Operation(summary = "Buscar indicador de conformidade por período")
    fun getCompliance(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<ComplianceIndicatorResponse?>> {
        val response = indicatorService.getComplianceByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/compliance/sector/{sectorId}")
    @Operation(summary = "Buscar indicadores de conformidade por setor")
    fun getComplianceBySector(@PathVariable sectorId: UUID): ResponseEntity<ApiResponse<List<ComplianceIndicatorResponse>>> {
        val response = indicatorService.getComplianceBySector(sectorId)
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
    @Operation(summary = "Salvar avaliação de higiene das mãos", description = "Cria uma nova avaliação de higiene das mãos.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun saveHandHygiene(@RequestBody request: HandHygieneRequest): ResponseEntity<ApiResponse<HandHygieneResponse>> {
        val response = indicatorService.saveHandHygiene(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de higiene das mãos salva com sucesso"))
    }

    @PutMapping("/hand-hygiene/{id}")
    @Operation(summary = "Atualizar avaliação de higiene das mãos", description = "Atualiza uma avaliação de higiene das mãos existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updateHandHygiene(@PathVariable id: UUID, @RequestBody request: HandHygieneRequest): ResponseEntity<ApiResponse<HandHygieneResponse>> {
        val response = indicatorService.updateHandHygiene(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de higiene das mãos atualizada com sucesso"))
    }

    @DeleteMapping("/hand-hygiene/{id}")
    @Operation(summary = "Deletar avaliação de higiene das mãos", description = "Remove permanentemente uma avaliação de higiene das mãos.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deleteHandHygiene(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        indicatorService.deleteHandHygiene(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Avaliação de higiene das mãos deletada com sucesso"))
    }

    @GetMapping("/hand-hygiene/period/{periodId}")
    @Operation(summary = "Buscar avaliação de higiene das mãos por período")
    fun getHandHygiene(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<HandHygieneResponse?>> {
        val response = indicatorService.getHandHygieneByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/hand-hygiene/sector/{sectorId}")
    @Operation(summary = "Buscar avaliações de higiene das mãos por setor")
    fun getHandHygieneBySector(@PathVariable sectorId: UUID): ResponseEntity<ApiResponse<List<HandHygieneResponse>>> {
        val response = indicatorService.getHandHygieneBySector(sectorId)
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
    @Operation(summary = "Salvar avaliação de risco de queda", description = "Cria uma nova avaliação de risco de queda.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun saveFallRisk(@RequestBody request: FallRiskRequest): ResponseEntity<ApiResponse<FallRiskResponse>> {
        val response = indicatorService.saveFallRisk(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de risco de queda salva com sucesso"))
    }

    @PutMapping("/fall-risk/{id}")
    @Operation(summary = "Atualizar avaliação de risco de queda", description = "Atualiza uma avaliação de risco de queda existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updateFallRisk(@PathVariable id: UUID, @RequestBody request: FallRiskRequest): ResponseEntity<ApiResponse<FallRiskResponse>> {
        val response = indicatorService.updateFallRisk(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de risco de queda atualizada com sucesso"))
    }

    @DeleteMapping("/fall-risk/{id}")
    @Operation(summary = "Deletar avaliação de risco de queda", description = "Remove permanentemente uma avaliação de risco de queda.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deleteFallRisk(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        indicatorService.deleteFallRisk(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Avaliação de risco de queda deletada com sucesso"))
    }

    @GetMapping("/fall-risk/period/{periodId}")
    @Operation(summary = "Buscar avaliação de risco de queda por período")
    fun getFallRisk(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<FallRiskResponse?>> {
        val response = indicatorService.getFallRiskByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/fall-risk/sector/{sectorId}")
    @Operation(summary = "Buscar avaliações de risco de queda por setor")
    fun getFallRiskBySector(@PathVariable sectorId: UUID): ResponseEntity<ApiResponse<List<FallRiskResponse>>> {
        val response = indicatorService.getFallRiskBySector(sectorId)
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
    @Operation(summary = "Salvar avaliação de risco de lesão por pressão", description = "Cria uma nova avaliação de risco de lesão por pressão.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun savePressureInjury(@RequestBody request: PressureInjuryRiskRequest): ResponseEntity<ApiResponse<PressureInjuryRiskResponse>> {
        val response = indicatorService.savePressureInjuryRisk(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Avaliação de risco de lesão por pressão salva com sucesso"))
    }

    @PutMapping("/pressure-injury/{id}")
    @Operation(summary = "Atualizar avaliação de risco de lesão por pressão", description = "Atualiza uma avaliação de risco de lesão por pressão existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updatePressureInjury(@PathVariable id: UUID, @RequestBody request: PressureInjuryRiskRequest): ResponseEntity<ApiResponse<PressureInjuryRiskResponse>> {
        val response = indicatorService.updatePressureInjuryRisk(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Avaliação de risco de lesão por pressão atualizada com sucesso"))
    }

    @DeleteMapping("/pressure-injury/{id}")
    @Operation(summary = "Deletar avaliação de risco de lesão por pressão", description = "Remove permanentemente uma avaliação de risco de lesão por pressão.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Avaliação deletada com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deletePressureInjury(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        indicatorService.deletePressureInjuryRisk(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Avaliação de risco de lesão por pressão deletada com sucesso"))
    }

    @GetMapping("/pressure-injury/period/{periodId}")
    @Operation(summary = "Buscar avaliação de risco de lesão por pressão por período")
    fun getPressureInjury(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<PressureInjuryRiskResponse?>> {
        val response = indicatorService.getPressureInjuryRiskByPeriod(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/pressure-injury/sector/{sectorId}")
    @Operation(summary = "Buscar avaliações de risco de lesão por pressão por setor")
    fun getPressureInjuryBySector(@PathVariable sectorId: UUID): ResponseEntity<ApiResponse<List<PressureInjuryRiskResponse>>> {
        val response = indicatorService.getPressureInjuryRiskBySector(sectorId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/pressure-injury/{id}")
    @Operation(summary = "Buscar avaliação de risco de lesão por pressão por ID")
    fun getPressureInjuryById(@PathVariable id: UUID): ResponseEntity<ApiResponse<PressureInjuryRiskResponse?>> {
        val response = indicatorService.getPressureInjuryRiskById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    // Meta Compliance
    @PostMapping("/meta-compliance")
    @Operation(summary = "Criar indicador de meta de identificação", description = "Cria um novo indicador de meta de identificação para um período e setor.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Indicador criado com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Indicador já existe para este período")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun createMeta(@RequestBody request: CreateMetaComplianceRequest): ResponseEntity<ApiResponse<MetaComplianceResponse>> {
        val response = newComplianceService.createMetaCompliance(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Indicador criado com sucesso"))
    }

    @PutMapping("/meta-compliance/{id}")
    @Operation(summary = "Atualizar indicador de meta de identificação", description = "Atualiza os valores de meta e porcentagem de um indicador existente.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador atualizado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updateMeta(
        @Parameter(description = "ID do indicador", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateMetaComplianceRequest
    ): ResponseEntity<ApiResponse<MetaComplianceResponse>> {
        val response = newComplianceService.updateMetaCompliance(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Indicador atualizado com sucesso"))
    }

    @DeleteMapping("/meta-compliance/{id}")
    @Operation(summary = "Deletar indicador de meta de identificação", description = "Remove permanentemente um indicador de meta de identificação.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador deletado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deleteMeta(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        newComplianceService.deleteMetaCompliance(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Indicador deletado com sucesso"))
    }

    @GetMapping("/meta-compliance/period/{periodId}")
    @Operation(summary = "Buscar indicador de meta por período", description = "Retorna o indicador de meta de identificação associado a um período específico.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador encontrado"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado")
    ])
    fun findMetaByPeriod(
        @Parameter(description = "ID do período", required = true)
        @PathVariable periodId: UUID
    ): ResponseEntity<ApiResponse<MetaComplianceResponse?>> {
        val response = newComplianceService.findMetaComplianceByPeriodId(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    // Medication Compliance
    @PostMapping("/medication-compliance")
    @Operation(summary = "Criar indicador de conformidade de medicamentos", description = "Cria um novo indicador de conformidade de medicamentos.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "201", description = "Indicador criado com sucesso"),
        SwaggerApiResponse(responseCode = "400", description = "Dados inválidos"),
        SwaggerApiResponse(responseCode = "409", description = "Indicador já existe para este período")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun createMedication(@RequestBody request: CreateMedicationComplianceRequest): ResponseEntity<ApiResponse<MedicationComplianceResponse>> {
        val response = newComplianceService.createMedicationCompliance(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Indicador criado com sucesso"))
    }

    @PutMapping("/medication-compliance/{id}")
    @Operation(summary = "Atualizar indicador de conformidade de medicamentos", description = "Atualiza a porcentagem de conformidade de medicamentos.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador atualizado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun updateMedication(
        @Parameter(description = "ID do indicador", required = true)
        @PathVariable id: UUID,
        @RequestBody request: UpdateMedicationComplianceRequest
    ): ResponseEntity<ApiResponse<MedicationComplianceResponse>> {
        val response = newComplianceService.updateMedicationCompliance(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Indicador atualizado com sucesso"))
    }

    @DeleteMapping("/medication-compliance/{id}")
    @Operation(summary = "Deletar indicador de conformidade de medicamentos", description = "Remove permanentemente um indicador de conformidade de medicamentos.")
    @ApiResponses(value = [
        SwaggerApiResponse(responseCode = "200", description = "Indicador deletado com sucesso"),
        SwaggerApiResponse(responseCode = "404", description = "Indicador não encontrado"),
        SwaggerApiResponse(responseCode = "409", description = "Período fechado")
    ])
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    fun deleteMedication(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        newComplianceService.deleteMedicationCompliance(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Indicador deletado com sucesso"))
    }

    @GetMapping("/medication-compliance/period/{periodId}")
    @Operation(summary = "Buscar indicador de medicamentos por período")
    fun findMedicationByPeriod(@PathVariable periodId: UUID): ResponseEntity<ApiResponse<MedicationComplianceResponse?>> {
        val response = newComplianceService.findMedicationComplianceByPeriodId(periodId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
