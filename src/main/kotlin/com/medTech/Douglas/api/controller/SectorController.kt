package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.sector.CreateSectorRequest
import com.medTech.Douglas.api.dto.sector.SectorResponse
import com.medTech.Douglas.api.dto.sector.UpdateSectorRequest
import com.medTech.Douglas.service.SectorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/sectors")
@Tag(name = "Setores", description = "APIs de Gestão de Setores")
class SectorController(
    private val sectorService: SectorService
) {

    @PostMapping
    @Operation(summary = "Criar um novo setor")
    fun create(@RequestBody request: CreateSectorRequest): ResponseEntity<ApiResponse<SectorResponse>> {
        val response = sectorService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Setor criado com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um setor existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateSectorRequest
    ): ResponseEntity<ApiResponse<SectorResponse>> {
        val response = sectorService.update(id, request)
        return ResponseEntity.ok(ApiResponse.success(response, "Setor atualizado com sucesso"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um setor")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        sectorService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Setor excluído com sucesso"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar setor por ID")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<SectorResponse>> {
        val response = sectorService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    @Operation(summary = "Listar todos os setores ativos")
    fun listActive(): ResponseEntity<ApiResponse<List<SectorResponse>>> {
        val response = sectorService.listActive()
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
