package com.example.culinair.domain.usecase.profile

import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        displayName: String,
        bio: String,
        website: String,
        instagram: String,
        twitter: String
    ): Result<Unit> {
        return try {
            repository.updateProfileInfo(displayName, bio, website, instagram, twitter)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update profile. Please try again."))
        }
    }
}