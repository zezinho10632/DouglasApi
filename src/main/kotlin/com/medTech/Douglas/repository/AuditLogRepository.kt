package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, UUID> {
    @Query("""
        SELECT a FROM AuditLog a 
        WHERE (cast(:action as text) IS NULL OR a.action = :action) 
        AND (cast(:resource as text) IS NULL OR a.resource = :resource) 
        AND (cast(:userEmail as text) IS NULL OR a.userEmail LIKE %:userEmail%)
        AND (cast(:startDate as timestamp) IS NULL OR a.createdAt >= :startDate)
        AND (cast(:endDate as timestamp) IS NULL OR a.createdAt <= :endDate)
        ORDER BY a.createdAt DESC
    """)
    fun search(
        action: String?,
        resource: String?,
        userEmail: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<AuditLog>
}
