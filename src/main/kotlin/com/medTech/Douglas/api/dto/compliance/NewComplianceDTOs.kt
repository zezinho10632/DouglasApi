package com.medTech.Douglas.api.dto.compliance

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

data class CreateMetaComplianceRequest(
    @field:Schema(description = "ID do Período", example = "uuid-do-periodo")
    val periodId: UUID,

    @field:Schema(description = "ID do Setor", example = "uuid-do-setor")
    val sectorId: UUID,

    @field:Schema(description = "Valor da Meta", example = "90.0")
    val goalValue: BigDecimal,

    @field:Schema(description = "Porcentagem alcançada", example = "85.5")
    val percentage: BigDecimal
)

data class UpdateMetaComplianceRequest(
    @field:Schema(description = "Valor da Meta", example = "90.0")
    val goalValue: BigDecimal,

    @field:Schema(description = "Porcentagem alcançada", example = "85.5")
    val percentage: BigDecimal
)

data class MetaComplianceResponse(
    val id: UUID,
    val periodId: UUID,
    val sectorId: UUID,
    val goalValue: BigDecimal,
    val percentage: BigDecimal,
    val createdBy: UUID?
)

data class CreateMedicationComplianceRequest(
    @field:Schema(description = "ID do Período", example = "uuid-do-periodo")
    val periodId: UUID,

    @field:Schema(description = "ID do Setor", example = "uuid-do-setor")
    val sectorId: UUID,

    @field:Schema(description = "Porcentagem de conformidade", example = "95.0")
    val percentage: BigDecimal
)

data class UpdateMedicationComplianceRequest(
    @field:Schema(description = "Porcentagem de conformidade", example = "95.0")
    val percentage: BigDecimal
)

data class MedicationComplianceResponse(
    val id: UUID,
    val periodId: UUID,
    val sectorId: UUID,
    val percentage: BigDecimal,
    val createdBy: UUID?
)
