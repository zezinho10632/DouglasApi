package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.auth.LoginRequest
import com.medTech.Douglas.api.dto.auth.LoginResponse
import com.medTech.Douglas.api.dto.auth.RegisterUserRequest
import com.medTech.Douglas.service.usecase.auth.LoginUseCase
import com.medTech.Douglas.service.usecase.auth.RegisterUserUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "APIs de Autenticação e Registro")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) {

    @PostMapping("/login")
    @Operation(summary = "Realizar login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = loginUseCase.execute(request)
        return ResponseEntity.ok(ApiResponse.success(response, "Login realizado com sucesso"))
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    fun register(@RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<Unit>> {
        registerUserUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(null, "Usuário registrado com sucesso"))
    }
}
