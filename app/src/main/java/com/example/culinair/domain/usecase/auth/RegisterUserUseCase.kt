package com.example.culinair.domain.usecase.auth

import com.example.culinair.data.repository.RegisterResult
import com.example.culinair.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<RegisterResult> {
        return repository.signUp(email, password)
    }
}