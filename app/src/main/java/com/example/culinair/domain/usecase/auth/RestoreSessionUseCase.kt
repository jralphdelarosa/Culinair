package com.example.culinair.domain.usecase.auth

import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/28/2025.
 */
@Singleton
class RestoreSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserSession?> {
        return authRepository.restoreSession()
    }
}