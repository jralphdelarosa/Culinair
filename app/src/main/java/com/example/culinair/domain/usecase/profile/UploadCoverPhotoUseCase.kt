package com.example.culinair.domain.usecase.profile

import android.net.Uri
import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/28/2025.
 */
@Singleton
class UploadCoverPhotoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return profileRepository.uploadCoverPhotoRaw(uri)
    }
}