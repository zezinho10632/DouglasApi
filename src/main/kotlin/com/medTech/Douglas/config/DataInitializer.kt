package com.medTech.Douglas.config

import com.medTech.Douglas.domain.entity.*
import com.medTech.Douglas.domain.enums.JobTitle
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
    private val classificationRepository: NotificationClassificationRepository,
    private val professionalCategoryRepository: ProfessionalCategoryRepository,
    private val selfNotificationRepository: SelfNotificationRepository,
    private val metaRepository: MetaComplianceRepository,
    private val medicationRepository: MedicationComplianceRepository,
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
                createSelfNotificationIfNotFound(period.id, sector.id, adminUser)
                createNewComplianceIfNotFound(period.id, sector.id, adminUser)
            }
        }
    }
    
    private fun createSelfNotificationIfNotFound(periodId: UUID, sectorId: UUID, adminUser: User?) {
        if (selfNotificationRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Mock Self Notification...")
            val selfNotification = SelfNotification(
                periodId = periodId,
                sectorId = sectorId,
                quantity = 20,
                percentage = BigDecimal("71.00"),
                createdBy = adminUser?.id
            )
            selfNotificationRepository.save(selfNotification)
        }
    }

    private fun createNewComplianceIfNotFound(periodId: UUID, sectorId: UUID, adminUser: User?) {
        // Meta Compliance
        if (metaRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Mock Meta Compliance...")
            val meta = MetaCompliance(
                periodId = periodId,
                sectorId = sectorId,
                goalValue = BigDecimal("100.00"),
                percentage = BigDecimal("93.00"),
                createdBy = adminUser?.id
            )
            metaRepository.save(meta)
        }

        // Medication Compliance
        if (medicationRepository.findByPeriodId(periodId) == null) {
            logger.info("Creating Mock Medication Compliance...")
            val medication = MedicationCompliance(
                periodId = periodId,
                sectorId = sectorId,
                percentage = BigDecimal("100.00"),
                createdBy = adminUser?.id
            )
            medicationRepository.save(medication)
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
                compliancePercentage = BigDecimal("90.0")
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
            
            // Create Classifications
            val class1 = classificationRepository.findByName("Incidente sem dano") 
                ?: classificationRepository.save(NotificationClassification(name = "Incidente sem dano"))
            val class2 = classificationRepository.findByName("Circunstância de Risco") 
                ?: classificationRepository.save(NotificationClassification(name = "Circunstância de Risco"))

            // Create Professional Categories
            val prof1 = professionalCategoryRepository.findByName("Enfermeiro")
                ?: professionalCategoryRepository.save(ProfessionalCategory(name = "Enfermeiro"))
            val prof2 = professionalCategoryRepository.findByName("Técnico de Enfermagem")
                ?: professionalCategoryRepository.save(ProfessionalCategory(name = "Técnico de Enfermagem"))

            // Notification 1: Incident without harm
            val notif1 = Notification(
                periodId = periodId,
                sectorId = sectorId,
                classification = class1,
                description = "Falha na assistência de enfermagem",
                professionalCategory = prof1,
                quantityClassification = 51,
                quantityCategory = 51,
                quantityProfessional = 10,
                quantity = 51,
                createdBy = adminUser?.id
            )
            notificationRepository.save(notif1)

            // Notification 2: Risk Circumstance
            val notif2 = Notification(
                periodId = periodId,
                sectorId = sectorId,
                classification = class2,
                description = "Lesão por pressão",
                professionalCategory = prof2,
                quantityClassification = 23,
                quantityCategory = 23,
                quantityProfessional = 5,
                quantity = 23,
                createdBy = adminUser?.id
            )
            notificationRepository.save(notif2)
        }
    }
}
