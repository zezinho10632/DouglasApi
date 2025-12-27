package com.medTech.Douglas.domain.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "professional_categories")
class ProfessionalCategory(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var active: Boolean = true
)
