package com.medTech.Douglas.service

interface PasswordHashingService {
    fun hash(password: String): String
    fun matches(rawPassword: String, hashedPassword: String): Boolean
}
