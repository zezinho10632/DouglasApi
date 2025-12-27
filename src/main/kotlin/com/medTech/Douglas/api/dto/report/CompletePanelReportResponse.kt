package com.medTech.Douglas.api.dto.report

import com.medTech.Douglas.api.dto.adverseevent.AdverseEventResponse
import com.medTech.Douglas.api.dto.compliance.MedicationComplianceResponse
import com.medTech.Douglas.api.dto.compliance.MetaComplianceResponse
import com.medTech.Douglas.api.dto.indicator.ComplianceIndicatorResponse
import com.medTech.Douglas.api.dto.indicator.FallRiskResponse
import com.medTech.Douglas.api.dto.indicator.HandHygieneResponse
import com.medTech.Douglas.api.dto.indicator.PressureInjuryRiskResponse
import com.medTech.Douglas.api.dto.notification.NotificationResponse
import com.medTech.Douglas.api.dto.selfnotification.SelfNotificationResponse

data class CompletePanelReportResponse(
    val complianceIndicator: ComplianceIndicatorResponse?,
    val handHygieneAssessment: HandHygieneResponse?,
    val fallRiskAssessment: FallRiskResponse?,
    val pressureInjuryRiskAssessment: PressureInjuryRiskResponse?,
    val selfNotification: SelfNotificationResponse?,
    val metaCompliance: MetaComplianceResponse?,
    val medicationCompliance: MedicationComplianceResponse?,
    val adverseEvents: List<AdverseEventResponse>,
    val notifications: List<NotificationResponse>
)
