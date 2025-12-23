package com.medTech.Douglas.service.validation

import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.exception.ClosedPeriodException
import com.medTech.Douglas.exception.PeriodNotFoundException
import com.medTech.Douglas.repository.PeriodRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PeriodValidator(
    private val periodRepository: PeriodRepository
) {
    fun validatePeriodIsOpen(periodId: UUID) {
        val period = periodRepository.findById(periodId)
            .orElseThrow { PeriodNotFoundException("Period not found with id: $periodId") }
        
        if (period.status != PeriodStatus.OPEN) {
            throw ClosedPeriodException("Cannot insert/update data in a closed period")
        }
    }
}
