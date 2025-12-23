package com.medTech.Douglas.service

import com.medTech.Douglas.domain.entity.AuditLog
import com.medTech.Douglas.repository.AuditLogRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {

    @Transactional(readOnly = true)
    fun search(
        action: String?,
        resource: String?,
        userEmail: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<AuditLog> {
        return auditLogRepository.search(action, resource, userEmail, startDate, endDate)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun log(action: String, resource: String, resourceId: String?, details: String?) {
        val auth = SecurityContextHolder.getContext().authentication
        val email = if (auth != null && auth.isAuthenticated && auth.name != "anonymousUser") {
            auth.name
        } else {
            "SYSTEM"
        }
        
        // Note: Getting User ID would require looking up the user, which adds overhead. 
        // For now, we store email which is readily available in the token/context.
        // If ID is strictly required, we can inject UserRepository.

        val log = AuditLog(
            userId = null, // Can be populated if we fetch user
            userEmail = email,
            action = action,
            resource = resource,
            resourceId = resourceId,
            details = details
        )
        auditLogRepository.save(log)
    }
}
