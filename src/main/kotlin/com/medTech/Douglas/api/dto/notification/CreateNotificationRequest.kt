package com.medTech.Douglas.api.dto.notification

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class CreateNotificationRequest(
    @field:Schema(description = "ID do Período", example = "uuid-do-periodo")
    val periodId: UUID,

    @field:Schema(description = "ID do Setor", example = "uuid-do-setor")
    val sectorId: UUID,

    @field:Schema(description = "ID da Classificação", example = "uuid-da-classificacao")
    val classificationId: UUID,

    @field:Schema(description = "ID da Categoria", example = "uuid-da-categoria")
    val categoryId: UUID,

    @field:Schema(description = "ID da Categoria Profissional (Opcional)", example = "uuid-da-categoria-profissional")
    val professionalCategoryId: UUID?,

    @field:Schema(description = "Quantidade para Classificação (Incidente)", example = "10")
    val quantityClassification: Int,

    @field:Schema(description = "Quantidade para Categoria (Descrição)", example = "10")
    val quantityCategory: Int,

    @field:Schema(description = "Quantidade para Profissional (Notificantes)", example = "10")
    val quantityProfessional: Int
)
