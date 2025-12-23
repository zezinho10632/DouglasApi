package com.medTech.Douglas.domain.entity

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role
import com.medTech.Douglas.exception.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

@Entity
@Table(name = "users")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var name: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.MANAGER,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_title")
    var jobTitle: JobTitle? = null,

    @Column(name = "sector_id")
    var sectorId: UUID? = null,

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
        if (email.isBlank()) {
            throw ValidationException("Email cannot be blank")
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ValidationException("Invalid email format")
        }
        if (name.isBlank()) {
            throw ValidationException("Name cannot be blank")
        }
        if (passwordHash.isBlank()) {
            throw ValidationException("Password cannot be blank")
        }
    }

    fun update(name: String, role: Role, sectorId: UUID?) {
        this.name = name
        this.role = role
        this.sectorId = sectorId
        this.updatedAt = LocalDateTime.now()
        validate()
    }

    fun changePassword(newPasswordHash: String) {
        if (newPasswordHash.isBlank()) {
            throw ValidationException("Password cannot be blank")
        }
        this.passwordHash = newPasswordHash
        this.updatedAt = LocalDateTime.now()
    }

    fun activate() {
        this.active = true
        this.updatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        this.active = false
        this.updatedAt = LocalDateTime.now()
    }

    companion object {
        private val EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )
    }
}
