package com.medTech.Douglas.config

import com.medTech.Douglas.domain.entity.*
import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.NotificationClassification
import com.medTech.Douglas.domain.enums.PeriodStatus
import com.medTech.Douglas.domain.enums.Role
import com.medTech.Douglas.repository.*
import com.medTech.Douglas.service.PasswordHashingService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val sectorRepository: SectorRepository,
    private val periodRepository: PeriodRepository,
    private val complianceRepository: ComplianceIndicatorRepository,
    private val handHygieneRepository: HandHygieneAssessmentRepository,
    private val fallRiskRepository: FallRiskAssessmentRepository,
    private val pressureInjuryRepository: PressureInjuryRiskAssessmentRepository,
    private val notificationRepository: NotificationRepository,
    private val passwordHashingService: PasswordHashingService
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        val sector = createSectorIfNotFound()
        val adminUser = createAdminUserIfNotFound()
        
        if (sector != null) {
            val period = createPeriodIfNotFound(sector)
            if (period != null) {
                createIndicatorsIfNotFound(period.id, sector.id)
                createNotificationsIfNotFound(period.id, sector.id, adminUser)
            }
        }
    }
    
    private fun createSectorIfNotFound(): Sector? {
        val code = "UTI-01"
        return if (!sectorRepository.existsByCode(code)) {
            logger.info("Creating default Sector...")
            val sector = Sector(
                name = "UTI Adulto",
                code = code
            )
            sectorRepository.save(sector).also {
                logger.info("Default Sector created: ${it.name}")
            }
        } else {
            logger.info("Default Sector already exists.")
            sectorRepository.findByCode(code)
        }
    }
    
    private fun createPeriodIfNotFound(sector: Sector): Period? {
        val now = LocalDate.now()
        val month = now.monthValue
        val year = now.year
        
        return if (!periodRepository.existsBySectorIdAndMonthAndYear(sector.id, month, year)) {
             logger.info("Creating default Period...")
             val period = Period(
                 sectorId = sector.id,
                 month = month,
                 year = year,
                 status = PeriodStatus.OPEN
             )
             periodRepository.save(period).also {
                 logger.info("Default Period created for ${month}/${year}")
             }
        } else {
            logger.info("Default Period already exists.")
            periodRepository.findBySectorIdAndMonthAndYear(sector.id, month, year)
        }
    }

    private fun createAdminUserIfNotFound(): User? {
        val adminEmail = "admin@douglas.com"
        return if (!userRepository.existsByEmail(adminEmail)) {
            logger.info("Creating default Admin user...")
            val adminUser = User(
                email = adminEmail,
                name = "Administrator",
                passwordHash = passwordHashingService.hash("admin123"),
                role = Role.ADMIN,
                jobTitle = JobTitle.ADMIN
            )
            userRepository.save(adminUser).also {
                logger.info("Default Admin user created: $adminEmail / admin123")
            }
        } else {
            logger.info("Admin user already exists.")
            userRepository.findByEmail(adminEmail)
        }
    }

    private fun createIndicatorsIfNotFound(periodId: UUID, sectorId: UUID) {
        // Compliance
        if (complianceRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Compliance Indicator...")
            val compliance = ComplianceIndicator(
                periodId = periodId,
                sectorId = sectorId,
                completeWristband = BigDecimal("95.5"),
                patientCommunication = BigDecimal("88.0"),
                medicationIdentified = BigDecimal("92.5"),
                handHygieneAdherence = BigDecimal("85.0"),
                fallRiskAssessment = BigDecimal("90.0"),
                pressureInjuryRiskAssessment = BigDecimal("89.5"),
                totalPatients = 150,
                observations = "Mocked data"
            )
            complianceRepository.save(compliance)
        }

        // Hand Hygiene
        if (handHygieneRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Hand Hygiene Assessment...")
            val handHygiene = HandHygieneAssessment.create(
                periodId = periodId,
                sectorId = sectorId,
                totalObservations = 50,
                compliantObservations = 45
            )
            handHygieneRepository.save(handHygiene)
        }

        // Fall Risk
        if (fallRiskRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Fall Risk Assessment...")
            val fallRisk = FallRiskAssessment.create(
                periodId = periodId,
                sectorId = sectorId,
                totalPatients = 100,
                assessedOnAdmission = 98,
                highRisk = 10,
                mediumRisk = 25,
                lowRisk = 63,
                notAssessed = 2
            )
            fallRiskRepository.save(fallRisk)
        }

        // Pressure Injury
        if (pressureInjuryRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Pressure Injury Risk Assessment...")
            val pressureInjury = PressureInjuryRiskAssessment.create(
                periodId = periodId,
                sectorId = sectorId,
                totalPatients = 100,
                assessedOnAdmission = 95,
                highRisk = 5,
                mediumRisk = 15,
                lowRisk = 75,
                notAssessed = 5
            )
            pressureInjuryRepository.save(pressureInjury)
        }
    }

    private fun createNotificationsIfNotFound(periodId: UUID, sectorId: UUID, adminUser: User?) {
        if (notificationRepository.findByPeriodId(periodId).isEmpty()) {
            logger.info("Creating Mock Notifications...")
            
            // Notification 1: Incident without harm
            val notif1 = Notification(
                periodId = periodId,
                sectorId = sectorId,
                notificationDate = LocalDate.now().minusDays(2),
                classification = NotificationClassification.INCIDENT_WITHOUT_HARM,
                category = "Medication",
                subcategory = "Dispensing error",
                description = "Wrong medication dispensed but identified before administration.",
                isSelfNotification = false,
                professionalCategory = "Nurse",
                professionalName = "Nurse Joy",
                createdBy = adminUser?.id
            )
            notificationRepository.save(notif1)

            // Notification 2: Risk Circumstance (The one user mentioned potential issues with)
            val notif2 = Notification(
                periodId = periodId,
                sectorId = sectorId,
                notificationDate = LocalDate.now().minusDays(1),
                classification = NotificationClassification.RISK_CIRCUMSTANCE,
                category = "Infrastructure",
                subcategory = "Slippery floor",
                description = "Floor wet without signage near bed 4.",
                isSelfNotification = true,
                professionalCategory = "Physiotherapist",
                professionalName = "John Doe",
                createdBy = adminUser?.id
            )
            notificationRepository.save(notif2)
        }
    }
}
