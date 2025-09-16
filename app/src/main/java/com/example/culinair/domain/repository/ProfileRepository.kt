package com.example.culinair.domain.repository

import android.net.Uri
import com.example.culinair.data.remote.model.response.ProfileResponse
import com.example.culinair.domain.model.UserStats

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
interface ProfileRepository {

    suspend fun uploadAvatarRaw(uri: Uri): Result<String>

    suspend fun uploadCoverPhotoRaw(uri: Uri): Result<String>

    suspend fun updateProfileInfo(
        displayName: String,
        bio: String,
        website: String,
        instagram: String,
        twitter: String
    ): Result<Unit>

    suspend fun getProfile(): Result<ProfileResponse>

    suspend fun getProfileById(userId: String): Result<ProfileResponse>

    suspend fun getFollowerCount(userId: String): Result<Int>
    suspend fun getFollowingCount(userId: String): Result<Int>
    suspend fun checkIfFollowing(targetUserId: String): Result<Boolean>
    suspend fun toggleFollow(targetUserId: String): Result<Boolean>
    suspend fun getUserStats(userId: String): Result<UserStats>
}