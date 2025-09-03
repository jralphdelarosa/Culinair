package com.example.culinair.domain.usecase.profile

import com.example.culinair.domain.model.UserStats
import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/30/2025.
 */
@Singleton
class GetUserStatsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<UserStats> {
        return profileRepository.getUserStats(userId)
    }
}