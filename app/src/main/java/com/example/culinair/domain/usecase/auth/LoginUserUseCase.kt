package com.example.culinair.domain.usecase.auth

import com.example.culinair.data.remote.model.response.UserSession
import com.example.culinair.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {
        return repository.signIn(email, password)
    }
}