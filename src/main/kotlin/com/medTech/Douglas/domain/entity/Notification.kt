package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "notifications")
class Notification(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classification_id", nullable = true)
    var classification: NotificationClassification? = null,

    @Column(name = "classification_text", nullable = true)
    var classificationText: String? = null,

    @Column(nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professional_category_id", nullable = true)
    var professionalCategory: ProfessionalCategory? = null,

    @Column(name = "professional_category_text", nullable = true)
    var professionalCategoryText: String? = null,

    @Column(name = "quantity_classification", nullable = false)
    var quantityClassification: Int,

    @Column(name = "quantity_category", nullable = false)
    var quantityCategory: Int,

    @Column(name = "quantity_professional", nullable = false)
    var quantityProfessional: Int,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "created_by")
    var createdBy: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
