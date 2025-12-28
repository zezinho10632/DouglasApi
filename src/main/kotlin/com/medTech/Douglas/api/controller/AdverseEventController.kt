package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.api.dto.adverseevent.CreateAdverseEventRequest
import com.medTech.Douglas.api.dto.adverseevent.UpdateAdverseEventRequest
import com.medTech.Douglas.service.AdverseEventService

import com.medTech.Douglas.service.usecase.adverseevent.ListAdverseEventsByDateRangeUseCase
import com.medTech.Douglas.service.usecase.adverseevent.ListAdverseEventsByUserUseCase
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

import com.medTech.Douglas.domain.enums.EventType

@RestController
@RequestMapping("/api/v1/adverse-events")
@Tag(name = "Eventos Adversos", description = "APIs de Gestão de Eventos Adversos")
class AdverseEventController(
    private val adverseEventService: AdverseEventService,
    private val listAdverseEventsByDateRangeUseCase: ListAdverseEventsByDateRangeUseCase,
    private val listAdverseEventsByUserUseCase: ListAdverseEventsByUserUseCase
) {

    @PostMapping
    @Operation(summary = "Registrar um novo evento adverso")
    fun create(@RequestBody request: CreateAdverseEventRequest): ResponseEntity<ApiResponse<AdverseEventResponse>> {
        val response = adverseEventService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Evento adverso registrado com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um evento adverso existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateAdverseEventRequest
    ): ResponseEntity<ApiResponse<AdverseEventResponse>> {
        val response = adverseEventService.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Evento adverso atualizado com sucesso"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um evento adverso")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        adverseEventService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Evento adverso excluído com sucesso"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento adverso por ID")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<AdverseEventResponse>> {
        val response = adverseEventService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/period/{periodId}")
    @Operation(summary = "Listar eventos adversos por período", description = "Lista eventos adversos de um período, podendo filtrar pelo tipo do evento.")
    @PreAuthorize("hasRole('ADMIN')")
    fun listByPeriod(
        @Parameter(description = "ID do período", required = true)
        @PathVariable periodId: UUID,

        @Parameter(description = "Tipo do evento adverso (ex: FALL, MEDICATION_ERROR)", required = false)
        @RequestParam(required = false) eventType: EventType?
    ): ResponseEntity<ApiResponse<List<AdverseEventResponse>>> {
        val response = adverseEventService.listByPeriod(periodId, eventType)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/sector/{sectorId}")
    @Operation(summary = "Listar eventos adversos por setor", description = "Lista todos os eventos adversos de um setor.")
    @PreAuthorize("hasRole('ADMIN')")
    fun listBySector(
        @Parameter(description = "ID do setor", required = true)
        @PathVariable sectorId: UUID
    ): ResponseEntity<ApiResponse<List<AdverseEventResponse>>> {
        val response = adverseEventService.listBySector(sectorId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/range")
    @Operation(summary = "Listar eventos adversos por intervalo de datas")
    @PreAuthorize("hasRole('ADMIN')")
    fun listByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<ApiResponse<List<AdverseEventResponse>>> {
        val response = listAdverseEventsByDateRangeUseCase.execute(startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar eventos adversos criados por um usuário")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    fun listByUser(@PathVariable userId: UUID): ResponseEntity<ApiResponse<List<AdverseEventResponse>>> {
        val response = listAdverseEventsByUserUseCase.execute(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
