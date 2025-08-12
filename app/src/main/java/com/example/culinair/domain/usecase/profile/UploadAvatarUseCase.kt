package com.example.culinair.domain.usecase.profile

import android.net.Uri
import com.example.culinair.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
class UploadAvatarUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return try {
            repository.uploadAvatarRaw(uri)
        } catch (e: Exception) {
            Result.failure(Exception("Avatar upload failed. Please try again."))
        }
    }
}