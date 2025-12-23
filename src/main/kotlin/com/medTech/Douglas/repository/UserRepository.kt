package com.medTech.Douglas.repository

import com.medTech.Douglas.domain.entity.User
import com.medTech.Douglas.domain.enums.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}
