package com.medTech.Douglas.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangeUserPasswordRequest(
    @field:NotBlank(message = "New password cannot be blank")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val newPassword: String
)
