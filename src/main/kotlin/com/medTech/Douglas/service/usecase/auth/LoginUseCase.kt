package com.medTech.Douglas.service.usecase.auth

import com.medTech.Douglas.api.dto.auth.LoginRequest
import com.medTech.Douglas.api.dto.auth.LoginResponse
import com.medTech.Douglas.config.security.jwt.JwtTokenProvider
import com.medTech.Douglas.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class LoginUseCase(
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) {

    fun execute(request: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val token = tokenProvider.createToken(authentication)
        
        val user = userRepository.findByEmail(request.email) ?: throw RuntimeException("User not found")

        return LoginResponse(
            token = token,
            userId = user.id,
            name = user.name,
            email = user.email,
            role = user.role.name,
            jobTitle = user.jobTitle?.name
        )
    }
}
