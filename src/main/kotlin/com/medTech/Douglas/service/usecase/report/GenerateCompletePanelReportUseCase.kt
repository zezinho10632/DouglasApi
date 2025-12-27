package com.medTech.Douglas.service.usecase.report

import com.medTech.Douglas.api.dto.report.CompletePanelReportResponse
import com.medTech.Douglas.repository.*
import com.medTech.Douglas.service.mapper.AdverseEventMapper
import com.medTech.Douglas.service.mapper.IndicatorMapper
import com.medTech.Douglas.service.mapper.NotificationMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Component
class GenerateCompletePanelReportUseCase(
    private val complianceRepository: ComplianceIndicatorRepository,
    private val handHygieneRepository: HandHygieneAssessmentRepository,
    private val fallRiskRepository: FallRiskAssessmentRepository,
    private val pressureInjuryRepository: PressureInjuryRiskAssessmentRepository,
    private val adverseEventRepository: AdverseEventRepository,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val indicatorMapper: IndicatorMapper,
    private val adverseEventMapper: AdverseEventMapper,
    private val notificationMapper: NotificationMapper
) {

    @Transactional(readOnly = true)
    fun execute(
        periodId: UUID, 
        sectorId: UUID, 
        startDate: LocalDate? = null, 
        endDate: LocalDate? = null
    ): CompletePanelReportResponse {
        val compliance = complianceRepository.findByPeriodId(periodId)
            ?.let { indicatorMapper.toResponse(it) }

        val handHygiene = handHygieneRepository.findByPeriodId(periodId)
            ?.let { indicatorMapper.toResponse(it) }

        val fallRisk = fallRiskRepository.findByPeriodId(periodId)
            ?.let { indicatorMapper.toResponse(it) }

        val pressureInjury = pressureInjuryRepository.findByPeriodId(periodId)
            ?.let { indicatorMapper.toResponse(it) }

        val adverseEventsDomain = if (startDate != null && endDate != null) {
            adverseEventRepository.findByPeriodIdAndSectorIdAndEventDateBetween(periodId, sectorId, startDate, endDate)
        } else {
            adverseEventRepository.findByPeriodIdAndSectorId(periodId, sectorId)
        }

        val aeUserIds = adverseEventsDomain.mapNotNull { it.createdBy }.distinct()
        val aeUsers = userRepository.findAllById(aeUserIds).associateBy { it.id }
        val adverseEvents = adverseEventsDomain.map { adverseEventMapper.toResponse(it, aeUsers) }

        val notificationsDomain = notificationRepository.findByPeriodIdAndSectorId(periodId, sectorId)

        val notifications = notificationsDomain.map { notificationMapper.toResponse(it) }

        return CompletePanelReportResponse(
            complianceIndicator = compliance,
            handHygieneAssessment = handHygiene,
            fallRiskAssessment = fallRisk,
            pressureInjuryRiskAssessment = pressureInjury,
            adverseEvents = adverseEvents,
            notifications = notifications
        )
    }
}
