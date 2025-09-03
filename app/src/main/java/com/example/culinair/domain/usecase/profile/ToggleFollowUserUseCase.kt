package com.example.culinair.domain.usecase.profile

import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
@Singleton
class ToggleFollowUserUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(targetUserId: String): Result<Boolean> {
        if (targetUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Target user ID cannot be empty"))
        }

        return profileRepository.toggleFollow(targetUserId)
    }
}