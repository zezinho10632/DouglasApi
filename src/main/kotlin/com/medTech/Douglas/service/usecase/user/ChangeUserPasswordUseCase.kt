package com.medTech.Douglas.service.usecase.user

import com.medTech.Douglas.api.dto.user.ChangeUserPasswordRequest
import com.medTech.Douglas.exception.ResourceNotFoundException
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.service.PasswordHashingService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class ChangeUserPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordHashingService: PasswordHashingService
) {
    @Transactional
    fun execute(userId: UUID, request: ChangeUserPasswordRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val hashedPassword = passwordHashingService.hash(request.newPassword)
        user.changePassword(hashedPassword)
        
        userRepository.save(user)
    }
}
