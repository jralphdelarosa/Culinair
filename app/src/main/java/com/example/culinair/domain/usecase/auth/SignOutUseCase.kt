package com.example.culinair.domain.usecase.auth

import android.util.Log
import com.example.culinair.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            Log.d("SignOutUseCase", "Starting sign out process")

            val result = authRepository.signOut()

            if (result.isSuccess) {
                Log.d("SignOutUseCase", "Sign out successful")
            } else {
                Log.e("SignOutUseCase", "Sign out failed: ${result.exceptionOrNull()?.message}")
            }

            result
        } catch (e: Exception) {
            Log.e("SignOutUseCase", "Sign out use case exception", e)
            Result.failure(e)
        }
    }
}