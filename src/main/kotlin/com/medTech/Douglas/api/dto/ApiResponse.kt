package com.medTech.Douglas.api.dto

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val success: Boolean = true
) {
    companion object {
        fun <T> success(data: T?, message: String? = null): ApiResponse<T> {
            return ApiResponse(data, message, true)
        }
        
        fun <T> error(message: String?): ApiResponse<T> {
            return ApiResponse(null, message, false)
        }
    }
}
