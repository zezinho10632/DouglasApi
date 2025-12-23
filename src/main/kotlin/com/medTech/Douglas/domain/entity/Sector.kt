package com.medTech.Douglas.domain.entity

import com.medTech.Douglas.exception.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "sectors")
class Sector(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    val code: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        validate()
    }

    private fun validate() {
        if (name.isBlank()) {
            throw ValidationException("Sector name cannot be blank")
        }
        if (name.length > 255) {
            throw ValidationException("Sector name cannot be longer than 255 characters")
        }
        if (code.isBlank()) {
            throw ValidationException("Sector code cannot be blank")
        }
    }

    fun update(name: String) {
        this.name = name
        this.updatedAt = LocalDateTime.now()
        validate()
    }

    fun activate() {
        this.active = true
        this.updatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        this.active = false
        this.updatedAt = LocalDateTime.now()
    }
}
