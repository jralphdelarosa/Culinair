package com.example.culinair.domain.repository

import com.example.culinair.data.repository.RegisterResult
import com.example.culinair.data.remote.dto.response.UserSession

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<RegisterResult>
    suspend fun signIn(email: String, password: String): Result<UserSession>
    suspend fun signInWithGoogle(idToken: String): Result<UserSession>
    suspend fun signOut(): Result<Unit>
    suspend fun ensureProfileExists(userId: String, token: String): Result<Unit>
}