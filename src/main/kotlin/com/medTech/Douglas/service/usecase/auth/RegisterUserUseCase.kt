package com.medTech.Douglas.service.usecase.auth

import com.medTech.Douglas.api.dto.auth.RegisterUserRequest
import com.medTech.Douglas.domain.entity.User
import com.medTech.Douglas.exception.BusinessRuleException
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.PasswordHashingService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordHashingService: PasswordHashingService
) {

    @Transactional
    fun execute(request: RegisterUserRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw BusinessRuleException("User with email ${request.email} already exists")
        }

        val hashedPassword = passwordHashingService.hash(request.password)
        val sectorId = request.sectorId?.let { UUID.fromString(it) }

        val user = User(
            email = request.email,
            name = request.name,
            passwordHash = hashedPassword,
            role = request.role,
            jobTitle = request.jobTitle,
            sectorId = sectorId
        )

        userRepository.save(user)
    }
}
