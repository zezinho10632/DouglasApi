package com.medTech.Douglas.service.usecase.report

import com.medTech.Douglas.api.dto.report.CompletePanelReportResponse
import com.medTech.Douglas.repository.PeriodRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Component
class GeneratePanelReportByRangeUseCase(
    private val periodRepository: PeriodRepository,
    private val generateCompletePanelReportUseCase: GenerateCompletePanelReportUseCase
) {

    @Transactional(readOnly = true)
    fun execute(
        sectorId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<CompletePanelReportResponse> {
        // Fetch all periods for the sector
        val allPeriods = periodRepository.search(sectorId, null, null)

        // Filter periods that fall within the range
        // A period is defined by month/year. We construct a LocalDate for the 1st of that month to compare.
        val periodsInRange = allPeriods.filter { period ->
            val periodDate = LocalDate.of(period.year, period.month, 1)
            // Check if periodDate is same month/year as start or end, or between them
            val startMonth = startDate.withDayOfMonth(1)
            val endMonth = endDate.withDayOfMonth(1)
            
            !periodDate.isBefore(startMonth) && !periodDate.isAfter(endMonth)
        }.sortedBy { LocalDate.of(it.year, it.month, 1) } // Sort ascending for the report

        // Generate report for each period
        return periodsInRange.map { period ->
            generateCompletePanelReportUseCase.execute(
                periodId = period.id,
                sectorId = sectorId,
                // We pass null for start/end date to the single report generator 
                // because we want the standard monthly data for that period.
                // If we passed the full range, it might fetch events from other months if the logic wasn't strict.
                // The current logic fetches events by periodId AND date range if provided.
                // To be safe and get strictly the period's data, we can either pass null (gets whole period)
                // or pass the specific start/end of that month.
                // Let's pass null to let the "Period" definition drive the data scope, 
                // which matches the dashboard requirement of "Jan", "Feb", "Mar" columns.
                startDate = null, 
                endDate = null
            )
        }
    }
}
