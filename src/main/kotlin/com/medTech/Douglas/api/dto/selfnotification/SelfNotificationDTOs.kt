package com.medTech.Douglas.api.dto.selfnotification

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

data class CreateSelfNotificationRequest(
    @field:Schema(description = "ID do Período", example = "uuid-do-periodo")
    val periodId: UUID,

    @field:Schema(description = "ID do Setor", example = "uuid-do-setor")
    val sectorId: UUID,

    @field:Schema(description = "Quantidade de auto-notificações", example = "20")
    val quantity: Int,

    @field:Schema(description = "Porcentagem de auto-notificações", example = "71.5")
    val percentage: BigDecimal
)

data class UpdateSelfNotificationRequest(
    @field:Schema(description = "Quantidade de auto-notificações", example = "20")
    val quantity: Int,

    @field:Schema(description = "Porcentagem de auto-notificações", example = "71.5")
    val percentage: BigDecimal
)

data class SelfNotificationResponse(
    val id: UUID,
    val periodId: UUID,
    val sectorId: UUID,
    val quantity: Int,
    val percentage: BigDecimal,
    val createdBy: UUID?
)
