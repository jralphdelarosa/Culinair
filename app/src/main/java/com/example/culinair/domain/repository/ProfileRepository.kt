package com.example.culinair.domain.repository

import android.net.Uri
import com.example.culinair.data.remote.dto.response.ProfileResponse

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
interface ProfileRepository {

    suspend fun uploadAvatarRaw(uri: Uri): Result<String>

    suspend fun updateProfileInfo(
        displayName: String,
        bio: String,
        website: String,
        instagram: String,
        twitter: String
    ): Result<Unit>

    suspend fun getProfile(): Result<ProfileResponse>
}