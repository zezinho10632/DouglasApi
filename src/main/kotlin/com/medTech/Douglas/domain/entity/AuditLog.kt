package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "audit_logs")
class AuditLog(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id")
    val userId: UUID?,

    @Column(name = "user_email")
    val userEmail: String?,

    @Column(nullable = false)
    val action: String,

    @Column(nullable = false)
    val resource: String,

    @Column(name = "resource_id")
    val resourceId: String?,

    @Column(columnDefinition = "TEXT")
    val details: String?,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
