package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "self_notifications")
class SelfNotification(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, precision = 5, scale = 2)
    var percentage: BigDecimal,

    @Column(name = "created_by")
    var createdBy: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
