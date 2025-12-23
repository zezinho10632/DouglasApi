package com.medTech.Douglas.service.usecase.user

import com.medTech.Douglas.repository.UserRepository
import com.medTech.Douglas.exception.ResourceNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    @Transactional
    fun execute(id: UUID) {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        
        user.deactivate()
        userRepository.save(user)
    }
}
