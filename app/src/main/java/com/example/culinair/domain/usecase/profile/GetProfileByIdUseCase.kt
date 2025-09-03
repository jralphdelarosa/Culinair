package com.example.culinair.domain.usecase.profile

import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/29/2025.
 */
@Singleton
class GetProfileByIdUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<ProfileResponse> {
        return profileRepository.getProfileById(userId = userId)
    }
}