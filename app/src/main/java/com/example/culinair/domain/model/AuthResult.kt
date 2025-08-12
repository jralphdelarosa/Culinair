package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.UserSession

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
sealed class AuthResult {
    data class Success(val userSession: UserSession) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}