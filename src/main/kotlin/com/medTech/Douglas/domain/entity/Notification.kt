package com.medTech.Douglas.domain.entity

import com.medTech.Douglas.domain.enums.NotificationClassification
import jakarta.persistence.*
import java.time.LocalDate
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

    @Column(name = "notification_date", nullable = false)
    var notificationDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var classification: NotificationClassification,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var subcategory: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "is_self_notification", nullable = false)
    var isSelfNotification: Boolean,

    @Column(name = "professional_category", nullable = false)
    var professionalCategory: String,

    @Column(name = "professional_name")
    var professionalName: String?,

    @Column(name = "created_by")
    var createdBy: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(
        notificationDate: LocalDate,
        classification: NotificationClassification,
        category: String,
        subcategory: String,
        description: String,
        isSelfNotification: Boolean,
        professionalCategory: String,
        professionalName: String?
    ) {
        this.notificationDate = notificationDate
        this.classification = classification
        this.category = category
        this.subcategory = subcategory
        this.description = description
        this.isSelfNotification = isSelfNotification
        this.professionalCategory = professionalCategory
        this.professionalName = professionalName
        this.updatedAt = LocalDateTime.now()
    }
}
