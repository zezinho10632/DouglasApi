package com.medTech.Douglas.api.controller

import com.medTech.Douglas.api.dto.ApiResponse
import com.medTech.Douglas.api.dto.user.UpdateUserRequest
import com.medTech.Douglas.api.dto.user.UserResponse
import com.medTech.Douglas.service.usecase.user.DeleteUserUseCase
import com.medTech.Douglas.service.usecase.user.ListUsersUseCase
import com.medTech.Douglas.service.usecase.user.UpdateUserUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

import com.medTech.Douglas.domain.enums.JobTitle
import com.medTech.Douglas.domain.enums.Role

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "APIs de Gestão de Usuários")
class UserController(
    private val listUsersUseCase: ListUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {

    @GetMapping
    @Operation(summary = "Listar todos os usuários com filtros opcionais")
    @PreAuthorize("hasRole('ADMIN')")
    fun listAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) role: Role?,
        @RequestParam(required = false) jobTitle: JobTitle?
    ): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val response = listUsersUseCase.execute(name, email, role, jobTitle)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val response = updateUserUseCase.execute(id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Unit>> {
        deleteUserUseCase.execute(id)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
