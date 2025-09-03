package com.example.culinair.presentation.viewmodel.other_profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.domain.model.UserStats
import com.example.culinair.domain.usecase.profile.GetProfileByIdUseCase
import com.example.culinair.domain.usecase.profile.GetUserStatsUseCase
import com.example.culinair.domain.usecase.profile.ToggleFollowUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/29/2025.
 */
@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val toggleFollowUserUseCase: ToggleFollowUserUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "OtherProfileViewModel"
    }

    // Profile data state
    var profile by mutableStateOf<ProfileResponse?>(null)
        private set

    var isLoadingProfile by mutableStateOf(false)
        private set

    var profileError by mutableStateOf<String?>(null)
        private set

    // User stats
    var userStats by mutableStateOf(UserStats())
        private set

    var isLoadingStats by mutableStateOf(false)
        private set

    // Follow action state
    var isFollowActionInProgress by mutableStateOf(false)
        private set

    // Form state
    var displayName by mutableStateOf("")
    var bio by mutableStateOf("")
    var website by mutableStateOf("")
    var instagram by mutableStateOf("")
    var twitter by mutableStateOf("")

    // Avatar state
    var avatarUrl by mutableStateOf<String?>(null)
        private set

    // Cover photo state
    var coverPhotoUrl by mutableStateOf<String?>(null)
        private set

    fun loadProfileById(userId: String) {
        Log.d(TAG, "🚀 Starting to load profile for userId: $userId")
        viewModelScope.launch {
            isLoadingProfile = true
            profileError = null

            // Load profile and stats concurrently
            Log.d(TAG, "🔄 Starting concurrent profile and stats loading...")
            val profileJob = async { getProfileByIdUseCase(userId) }
            val statsJob = async { getUserStatsUseCase(userId) }

            try {
                val profileResult = profileJob.await()
                val statsResult = statsJob.await()

                Log.d(TAG, "📊 Profile result success: ${profileResult.isSuccess}")
                Log.d(TAG, "📊 Stats result success: ${statsResult.isSuccess}")

                profileResult.onSuccess { profileData ->
                    profile = profileData
                    avatarUrl = profileData.avatarUrl
                    bio = profileData.bio ?: ""
                    displayName = profileData.displayName ?: "Unknown"
                    coverPhotoUrl = profileData.coverPhotoUrl
                    instagram = profileData.instagram ?: ""
                    twitter = profileData.twitter ?: ""
                    website = profileData.website ?: ""

                    Log.d(TAG, "✅ Profile loaded successfully:")
                    Log.d(TAG, "   - Display Name: ${profileData.displayName}")
                    Log.d(TAG, "   - Bio: ${profileData.bio}")
                    Log.d(TAG, "   - Avatar URL: ${profileData.avatarUrl}")
                    Log.d(TAG, "   - Cover Photo URL: ${profileData.coverPhotoUrl}")
                }.onFailure { error ->
                    profileError = error.message
                    Log.e(TAG, "❌ Failed to load profile for userId: $userId", error)
                    Log.e(TAG, "Error message: ${error.message}")
                }

                statsResult.onSuccess { stats ->
                    userStats = stats
                    Log.d(TAG, "✅ Stats loaded successfully:")
                    Log.d(TAG, "   - Followers: ${stats.followersCount}")
                    Log.d(TAG, "   - Following: ${stats.followingCount}")
                    Log.d(TAG, "   - Is Following: ${stats.isFollowing}")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Failed to load stats for userId: $userId", error)
                    Log.e(TAG, "Stats error message: ${error.message}")
                    // Don't show error for stats, just log it
                }

            } catch (e: Exception) {
                profileError = e.message
                Log.e(TAG, "💥 Exception during profile loading for userId: $userId", e)
            }

            isLoadingProfile = false
            Log.d(TAG, "🏁 Profile loading completed for userId: $userId")
        }
    }

    fun toggleFollow(userId: String) {
        Log.d(TAG, "🔄 Starting follow toggle for userId: $userId")
        Log.d(TAG, "Current follow status: ${userStats.isFollowing}")
        Log.d(TAG, "Current followers count: ${userStats.followersCount}")

        viewModelScope.launch {
            isFollowActionInProgress = true
            val result = toggleFollowUserUseCase(userId)
            result.onSuccess { isNowFollowing ->
                userStats = userStats.copy(isFollowing = isNowFollowing)
            }.onFailure {
                // Handle error
            }
            isFollowActionInProgress = false
        }

    }

    // Optional: Add a method to refresh stats manually (useful for debugging)
    fun refreshUserStats(userId: String) {
        Log.d(TAG, "🔄 Manual refresh of user stats for userId: $userId")
        viewModelScope.launch {
            isLoadingStats = true
            try {
                getUserStatsUseCase(userId).onSuccess { stats ->
                    userStats = stats
                    Log.d(TAG, "✅ Manual stats refresh successful: $stats")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Manual stats refresh failed", error)
                }
            } finally {
                isLoadingStats = false
            }
        }
    }

    // Optional: Add method to verify actual follow status against server
    fun verifyFollowStatus(userId: String) {
        Log.d(TAG, "🔍 Verifying follow status against server for userId: $userId")
        viewModelScope.launch {
            try {
                getUserStatsUseCase(userId).onSuccess { serverStats ->
                    val localFollowStatus = userStats.isFollowing
                    val serverFollowStatus = serverStats.isFollowing

                    if (localFollowStatus != serverFollowStatus) {
                        Log.w(TAG, "⚠️ Follow status mismatch detected!")
                        Log.w(TAG, "   Local status: $localFollowStatus")
                        Log.w(TAG, "   Server status: $serverFollowStatus")
                        Log.w(TAG, "   Syncing with server...")
                        userStats = serverStats
                        Log.d(TAG, "✅ Stats synced with server: $userStats")
                    } else {
                        Log.d(TAG, "✅ Follow status verified - local and server match: $localFollowStatus")
                    }
                }.onFailure { error ->
                    Log.e(TAG, "❌ Failed to verify follow status", error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception during follow status verification", e)
            }
        }
    }
}