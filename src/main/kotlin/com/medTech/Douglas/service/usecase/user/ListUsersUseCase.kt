package com.medTech.Douglas.service.usecase.user

import com.medTech.Douglas.api.dto.user.UserResponse
import com.medTech.Douglas.domain.entity.User
import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role
import com.medTech.Douglas.repository.UserRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListUsersUseCase(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun execute(name: String?, email: String?, role: Role?, jobTitle: JobTitle?): List<UserResponse> {
        val spec = Specification<User> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            if (!name.isNullOrBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%${name.lowercase()}%"))
            }

            if (!email.isNullOrBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%${email.lowercase()}%"))
            }

            if (role != null) {
                predicates.add(cb.equal(root.get<Role>("role"), role))
            }

            if (jobTitle != null) {
                predicates.add(cb.equal(root.get<JobTitle>("jobTitle"), jobTitle))
            }

            cb.and(*predicates.toTypedArray())
        }

        return userRepository.findAll(spec)
            .map { user ->
                UserResponse(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    role = user.role,
                    jobTitle = user.jobTitle,
                    sectorId = user.sectorId,
                    active = user.active,
                    createdAt = user.createdAt
                )
            }
    }
}
