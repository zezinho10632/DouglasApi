package com.medTech.Douglas.service.usecase.report

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.api.dto.indicator.*
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.report.CompletePanelReportResponse
import com.medTech.Douglas.domain.enums.ReportPeriodicity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

@Component
class GenerateCumulativePanelReportUseCase(
    private val generatePanelReportByRangeUseCase: GeneratePanelReportByRangeUseCase
) {

    fun execute(
        sectorId: UUID,
        periodicity: ReportPeriodicity,
        year: Int?,
        period: Int?, // Month (1-12), Quarter (1-4), Semester (1-2)
        customStartDate: LocalDate?,
        customEndDate: LocalDate?
    ): CompletePanelReportResponse {

        val (startDate, endDate) = calculateDateRange(periodicity, year, period, customStartDate, customEndDate)
        val reports = generatePanelReportByRangeUseCase.execute(sectorId, startDate, endDate)

        if (reports.isEmpty()) {
            return createEmptyReport()
        }

        return aggregateReports(reports)
    }

    private fun calculateDateRange(
        periodicity: ReportPeriodicity,
        year: Int?,
        period: Int?,
        customStartDate: LocalDate?,
        customEndDate: LocalDate?
    ): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        val currentYear = year ?: now.year

        return when (periodicity) {
            ReportPeriodicity.CUSTOM -> {
                if (customStartDate == null || customEndDate == null) {
                    throw IllegalArgumentException("Start and End dates are required for CUSTOM periodicity")
                }
                Pair(customStartDate, customEndDate)
            }
            ReportPeriodicity.MONTHLY -> {
                val month = period ?: now.monthValue
                val start = LocalDate.of(currentYear, month, 1)
                Pair(start, start.withDayOfMonth(start.lengthOfMonth()))
            }
            ReportPeriodicity.QUARTERLY -> {
                val quarter = period ?: ((now.monthValue - 1) / 3 + 1)
                val startMonth = (quarter - 1) * 3 + 1
                val start = LocalDate.of(currentYear, startMonth, 1)
                val end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth())
                Pair(start, end)
            }
            ReportPeriodicity.SEMESTRAL -> {
                val semester = period ?: if (now.monthValue <= 6) 1 else 2
                val startMonth = if (semester == 1) 1 else 7
                val start = LocalDate.of(currentYear, startMonth, 1)
                val end = start.plusMonths(5).withDayOfMonth(start.plusMonths(5).lengthOfMonth())
                Pair(start, end)
            }
            ReportPeriodicity.ANNUAL -> {
                val start = LocalDate.of(currentYear, 1, 1)
                val end = LocalDate.of(currentYear, 12, 31)
                Pair(start, end)
            }
        }
    }

    private fun aggregateReports(reports: List<CompletePanelReportResponse>): CompletePanelReportResponse {
        val compliance = aggregateCompliance(reports.mapNotNull { it.complianceIndicator })
        val handHygiene = aggregateHandHygiene(reports.mapNotNull { it.handHygieneAssessment })
        val fallRisk = aggregateFallRisk(reports.mapNotNull { it.fallRiskAssessment })
        val pressureInjury = aggregatePressureInjury(reports.mapNotNull { it.pressureInjuryRiskAssessment })
        val adverseEvents = reports.flatMap { it.adverseEvents }.sortedByDescending { it.eventDate }
        val notifications = reports.flatMap { it.notifications }.sortedByDescending { it.notificationDate }

        return CompletePanelReportResponse(
            complianceIndicator = compliance,
            handHygieneAssessment = handHygiene,
            fallRiskAssessment = fallRisk,
            pressureInjuryRiskAssessment = pressureInjury,
            adverseEvents = adverseEvents,
            notifications = notifications
        )
    }

    private fun aggregateCompliance(indicators: List<ComplianceIndicatorResponse>): ComplianceIndicatorResponse? {
        if (indicators.isEmpty()) return null

        val totalPatients = indicators.sumOf { it.totalPatients }
        if (totalPatients == 0) return indicators.first().copy(totalPatients = 0) // Or return empty with 0s

        // Helper to recalculate weighted average percentage
        fun weightedAvg(selector: (ComplianceIndicatorResponse) -> BigDecimal): BigDecimal {
            val weightedSum = indicators.sumOf { 
                it.totalPatients.toBigDecimal().multiply(selector(it)) 
            }
            return weightedSum.divide(totalPatients.toBigDecimal(), 2, RoundingMode.HALF_UP)
        }

        val observations = indicators.mapNotNull { it.observations }.joinToString("\n---\n")

        return ComplianceIndicatorResponse(
            id = "aggregated",
            periodId = "aggregated",
            sectorId = indicators.first().sectorId,
            totalPatients = totalPatients,
            completeWristband = weightedAvg { it.completeWristband },
            patientCommunication = weightedAvg { it.patientCommunication },
            medicationIdentified = weightedAvg { it.medicationIdentified },
            handHygieneAdherence = weightedAvg { it.handHygieneAdherence },
            fallRiskAssessment = weightedAvg { it.fallRiskAssessment },
            pressureInjuryRiskAssessment = weightedAvg { it.pressureInjuryRiskAssessment },
            observations = if (observations.isBlank()) null else observations,
            createdAt = LocalDate.now().toString()
        )
    }

    private fun aggregateHandHygiene(assessments: List<HandHygieneResponse>): HandHygieneResponse? {
        if (assessments.isEmpty()) return null

        val totalObs = assessments.sumOf { it.totalObservations }
        val compliantObs = assessments.sumOf { it.compliantObservations }
        
        val percentage = if (totalObs == 0) BigDecimal.ZERO 
            else BigDecimal(compliantObs).multiply(BigDecimal(100)).divide(BigDecimal(totalObs), 2, RoundingMode.HALF_UP)

        return HandHygieneResponse(
            id = "aggregated",
            periodId = "aggregated",
            sectorId = assessments.first().sectorId,
            totalObservations = totalObs,
            compliantObservations = compliantObs,
            compliancePercentage = percentage,
            createdAt = LocalDate.now().toString()
        )
    }

    private fun aggregateFallRisk(assessments: List<FallRiskResponse>): FallRiskResponse? {
        if (assessments.isEmpty()) return null

        val total = assessments.sumOf { it.totalPatients }
        val assessed = assessments.sumOf { it.assessedOnAdmission }
        val high = assessments.sumOf { it.highRisk }
        val medium = assessments.sumOf { it.mediumRisk }
        val low = assessments.sumOf { it.lowRisk }
        val notAssessed = assessments.sumOf { it.notAssessed }

        fun calcPerc(value: Int, total: Int): BigDecimal {
            if (total == 0) return BigDecimal.ZERO
            return BigDecimal(value).multiply(BigDecimal(100)).divide(BigDecimal(total), 2, RoundingMode.HALF_UP)
        }

        return FallRiskResponse(
            id = "aggregated",
            periodId = "aggregated",
            sectorId = assessments.first().sectorId,
            totalPatients = total,
            assessedOnAdmission = assessed,
            assessmentPercentage = calcPerc(assessed, total),
            highRisk = high,
            mediumRisk = medium,
            lowRisk = low,
            notAssessed = notAssessed,
            highRiskPercentage = calcPerc(high, total),
            mediumRiskPercentage = calcPerc(medium, total),
            lowRiskPercentage = calcPerc(low, total),
            notAssessedPercentage = calcPerc(notAssessed, total),
            createdAt = LocalDate.now().toString()
        )
    }

    private fun aggregatePressureInjury(assessments: List<PressureInjuryRiskResponse>): PressureInjuryRiskResponse? {
        if (assessments.isEmpty()) return null

        val total = assessments.sumOf { it.totalPatients }
        val assessed = assessments.sumOf { it.assessedOnAdmission }
        val high = assessments.sumOf { it.highRisk }
        val medium = assessments.sumOf { it.mediumRisk }
        val low = assessments.sumOf { it.lowRisk }
        val notAssessed = assessments.sumOf { it.notAssessed }

        fun calcPerc(value: Int, total: Int): BigDecimal {
            if (total == 0) return BigDecimal.ZERO
            return BigDecimal(value).multiply(BigDecimal(100)).divide(BigDecimal(total), 2, RoundingMode.HALF_UP)
        }

        return PressureInjuryRiskResponse(
            id = "aggregated",
            periodId = "aggregated",
            sectorId = assessments.first().sectorId,
            totalPatients = total,
            assessedOnAdmission = assessed,
            assessmentPercentage = calcPerc(assessed, total),
            highRisk = high,
            mediumRisk = medium,
            lowRisk = low,
            notAssessed = notAssessed,
            highRiskPercentage = calcPerc(high, total),
            mediumRiskPercentage = calcPerc(medium, total),
            lowRiskPercentage = calcPerc(low, total),
            notAssessedPercentage = calcPerc(notAssessed, total),
            createdAt = LocalDate.now().toString()
        )
    }

    private fun createEmptyReport(): CompletePanelReportResponse {
        return CompletePanelReportResponse(null, null, null, null, emptyList(), emptyList())
    }
}
