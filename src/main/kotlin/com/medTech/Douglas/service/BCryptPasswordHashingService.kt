package com.medTech.Douglas.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class BCryptPasswordHashingService : PasswordHashingService {
    private val encoder = BCryptPasswordEncoder()

    override fun hash(password: String): String {
        return encoder.encode(password)
    }

    override fun matches(rawPassword: String, hashedPassword: String): Boolean {
        return encoder.matches(rawPassword, hashedPassword)
    }
}
