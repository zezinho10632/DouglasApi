package com.medTech.Douglas.service.usecase.user

import com.medTech.Douglas.api.dto.user.UpdateUserRequest
import com.medTech.Douglas.api.dto.user.UserResponse
import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.exception.ResourceNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    @Transactional
    fun execute(id: UUID, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        // Update fields
        user.name = request.name
        user.role = request.role
        user.jobTitle = request.jobTitle
        user.sectorId = request.sectorId
        
        // Update active status if provided
        request.active?.let { active ->
            if (active) user.activate() else user.deactivate()
        }

        // The entity's update method handles validation and timestamp
        // But since we are setting properties directly to support jobTitle which wasn't in update(),
        // we should ensure validation is called or replicated.
        // User entity has init block validation, but not on property set automatically unless using custom setters.
        // However, the User.update method does validation. Let's use setters and manually trigger validation/update logic.
        
        // Calling update logic manually to ensure consistency
        user.updatedAt = java.time.LocalDateTime.now()
        
        val updatedUser = userRepository.save(user)

        return UserResponse(
            id = updatedUser.id,
            name = updatedUser.name,
            email = updatedUser.email,
            role = updatedUser.role,
            jobTitle = updatedUser.jobTitle,
            sectorId = updatedUser.sectorId,
            active = updatedUser.active,
            createdAt = updatedUser.createdAt
        )
    }
}
