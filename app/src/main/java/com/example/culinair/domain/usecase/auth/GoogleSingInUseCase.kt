package com.example.culinair.domain.usecase.auth

import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<UserSession> {
        return repository.signInWithGoogle(idToken)
    }
}