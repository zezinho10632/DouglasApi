package com.medTech.Douglas.domain.entity

import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.ClosedPeriodException
import com.medTech.Douglas.exception.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "periods", uniqueConstraints = [
    UniqueConstraint(columnNames = ["month", "year", "sector_id"])
])
class Period(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val month: Int,

    @Column(nullable = false)
    val year: Int,

    @Column(name = "sector_id", nullable = false)
    val sectorId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PeriodStatus = PeriodStatus.OPEN,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        validateInputs()
    }

    private fun validateInputs() {
        if (month < 1 || month > 12) {
            throw ValidationException("Month must be between 1 and 12")
        }
        if (year < 2000) {
            throw ValidationException("Year must be valid")
        }
    }

    fun close() {
        if (status == PeriodStatus.CLOSED) return
        this.status = PeriodStatus.CLOSED
        this.updatedAt = LocalDateTime.now()
    }

    fun validatePeriod() {
        if (status != PeriodStatus.CLOSED) {
            throw ClosedPeriodException("Period must be closed before validation")
        }
        this.status = PeriodStatus.VALIDATED
        this.updatedAt = LocalDateTime.now()
    }

    fun reopen() {
        if (status == PeriodStatus.VALIDATED) {
             throw ClosedPeriodException("Validated period cannot be reopened")
        }
        this.status = PeriodStatus.OPEN
        this.updatedAt = LocalDateTime.now()
    }
}
