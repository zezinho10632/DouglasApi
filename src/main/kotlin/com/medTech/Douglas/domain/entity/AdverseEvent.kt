package com.medTech.Douglas.domain.entity

import com.medTech.Douglas.domain.enums.EventType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "adverse_events")
class AdverseEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "period_id", nullable = false)
    val periodId: UUID,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Column(name = "event_date", nullable = false)
    var eventDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    var eventType: EventType,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "was_notified", nullable = false)
    var wasNotified: Boolean = false,

    @Column(name = "created_by")
    var createdBy: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun markAsNotified() {
        this.wasNotified = true
        this.updatedAt = LocalDateTime.now()
    }

    fun update(
        eventDate: LocalDate,
        eventType: EventType,
        description: String
    ) {
        this.eventDate = eventDate
        this.eventType = eventType
        this.description = description
        this.updatedAt = LocalDateTime.now()
    }
}
