package com.medTech.Douglas.api.dto.notification

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class CreateNotificationRequest(
    @field:Schema(description = "ID do Período", example = "uuid-do-periodo")
    val periodId: UUID,

    @field:Schema(description = "ID do Setor", example = "uuid-do-setor")
    val sectorId: UUID,

    @field:Schema(description = "ID da Classificação (Opcional)", example = "uuid-da-classificacao")
    val classificationId: UUID?,

    @field:Schema(description = "Texto da Classificação (Opcional - se ID não informado)", example = "Texto da classificação")
    val classificationText: String?,

    @field:Schema(description = "Descrição", example = "Texto da descrição")
    val description: String,

    @field:Schema(description = "ID da Categoria Profissional (Opcional)", example = "uuid-da-categoria-profissional")
    val professionalCategoryId: UUID?,

    @field:Schema(description = "Texto da Categoria Profissional (Opcional - se ID não informado)", example = "Texto da categoria profissional")
    val professionalCategoryText: String?,

    @field:Schema(description = "Quantidade para Classificação (Incidente)", example = "10")
    val quantityClassification: Int,

    @field:Schema(description = "Quantidade para Categoria (Descrição)", example = "10")
    val quantityCategory: Int,

    @field:Schema(description = "Quantidade para Profissional (Notificantes)", example = "10")
    val quantityProfessional: Int,

    @field:Schema(description = "Quantidade da Notificação", example = "1")
    val quantity: Int
)
