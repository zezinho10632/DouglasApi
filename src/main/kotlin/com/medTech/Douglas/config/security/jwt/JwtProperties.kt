package com.medTech.Douglas.config.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    var secret: String = "secretKeyshouldBeLongEnoughForHS256AlgorithmOrGreater"
    var expirationInMs: Long = 3600000 // 1 hour
}
