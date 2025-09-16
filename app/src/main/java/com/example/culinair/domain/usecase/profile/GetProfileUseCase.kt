package com.example.culinair.domain.usecase.profile

import com.example.culinair.data.remote.model.response.ProfileResponse
import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */

@Singleton
class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<ProfileResponse> {
        return profileRepository.getProfile()
    }
}