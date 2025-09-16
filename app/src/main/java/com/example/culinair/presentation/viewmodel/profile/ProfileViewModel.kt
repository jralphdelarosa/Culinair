package com.example.culinair.presentation.viewmodel.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.remote.model.response.ProfileResponse
import com.example.culinair.domain.model.UserStats
import com.example.culinair.domain.usecase.auth.RestoreSessionUseCase
import com.example.culinair.domain.usecase.profile.GetProfileUseCase
import com.example.culinair.domain.usecase.profile.GetUserStatsUseCase
import com.example.culinair.domain.usecase.profile.UpdateProfileUseCase
import com.example.culinair.domain.usecase.profile.UploadAvatarUseCase
import com.example.culinair.domain.usecase.profile.UploadCoverPhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val uploadCoverPhotoUseCase: UploadCoverPhotoUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val restoreSessionUseCase: RestoreSessionUseCase
) : ViewModel() {

    companion object{
        private const val TAG = "ProfileViewModel"
    }

    // Profile data state
    var profile by mutableStateOf<ProfileResponse?>(null)
        private set

    var isLoadingProfile by mutableStateOf(false)
        private set

    var profileError by mutableStateOf<String?>(null)
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

    var isUploading by mutableStateOf(false)
        private set

    var uploadError by mutableStateOf<String?>(null)
        private set

    // Cover photo state
    var coverPhotoUrl by mutableStateOf<String?>(null)
        private set

    var isUploadingCoverPhoto by mutableStateOf(false)
        private set

    var coverPhotoUploadError by mutableStateOf<String?>(null)
        private set

    // User stats
    var userStats by mutableStateOf(UserStats())
        private set

    var isLoadingStats by mutableStateOf(false)
        private set

    // Save state
    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    var saveError by mutableStateOf<String?>(null)
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            isLoadingProfile = true
            profileError = null

            restoreSessionUseCase()

            val result = getProfileUseCase()

            result.onSuccess { profileData ->
                profile = profileData

                // Pre-populate form fields AND avatar
                displayName = profileData.displayName ?: ""
                bio = profileData.bio ?: ""
                website = profileData.website ?: ""
                instagram = profileData.instagram ?: ""
                twitter = profileData.twitter ?: ""
                avatarUrl = profileData.avatarUrl
                coverPhotoUrl = profileData.coverPhotoUrl

                Log.d(TAG, "Profile loaded: $profileData")
                Log.d(TAG, "Avatar URL: ${profileData.avatarUrl}")
                Log.d(TAG, "Display Name: ${profileData.displayName}")

                val statsResult = getUserStatsUseCase(profile?.id ?: "")

                statsResult.onSuccess { stats ->
                    userStats = stats
                    Log.d(TAG, "✅ Stats loaded successfully:")
                    Log.d(TAG, "   - Followers: ${stats.followersCount}")
                    Log.d(TAG, "   - Following: ${stats.followingCount}")
                    Log.d(TAG, "   - Is Following: ${stats.isFollowing}")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Failed to load stats for userId: ${profile?.id ?: ""}", error)
                    Log.e(TAG, "Stats error message: ${error.message}")
                    // Don't show error for stats, just log it
                }

            }.onFailure { error ->
                profileError = error.message
                Log.e(TAG, "Failed to load profile", error)
            }

            isLoadingProfile = false
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            isUploading = true
            uploadError = null

            val result = uploadAvatarUseCase(uri)

            result.onSuccess { url ->
                avatarUrl = url  // Update avatar immediately

                val cacheBustedUrl = "$url&ui=${System.currentTimeMillis()}"
                avatarUrl = cacheBustedUrl

                profile = profile?.copy(avatarUrl = cacheBustedUrl)

                Log.d(TAG, "Avatar updated with cache bust: $cacheBustedUrl")

                // FIXED: Safe copy with null checks
                profile = profile?.let { currentProfile ->
                    try {
                        currentProfile.copy(avatarUrl = url)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to copy profile", e)
                        // Create new profile object safely
                        ProfileResponse(
                            id = currentProfile.id,
                            displayName = currentProfile.displayName,
                            bio = currentProfile.bio,
                            website = currentProfile.website,
                            instagram = currentProfile.instagram,
                            twitter = currentProfile.twitter,
                            avatarUrl = url,
                            coverPhotoUrl = currentProfile.coverPhotoUrl,
                            createdAt = currentProfile.createdAt,
                            updatedAt = currentProfile.updatedAt
                        )
                    }
                }

                Log.d(TAG, "Avatar uploaded successfully: $url")

            }.onFailure { error ->
                uploadError = error.message
                Log.e(TAG, "Avatar upload failed", error)
            }

            isUploading = false
        }
    }

    fun uploadCoverPhoto(uri: Uri) {
        viewModelScope.launch {
            isUploadingCoverPhoto = true
            coverPhotoUploadError = null

            val result = uploadCoverPhotoUseCase(uri)

            result.onSuccess { url ->
                coverPhotoUrl = url

                val cacheBustedUrl = "$url&ui=${System.currentTimeMillis()}"
                coverPhotoUrl = cacheBustedUrl

                profile = profile?.copy(coverPhotoUrl = cacheBustedUrl)

                Log.d(TAG, "Cover photo updated with cache bust: $cacheBustedUrl")

                profile = profile?.let { currentProfile ->
                    try {
                        currentProfile.copy(coverPhotoUrl = url)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to copy profile", e)
                        ProfileResponse(
                            id = currentProfile.id,
                            displayName = currentProfile.displayName,
                            bio = currentProfile.bio,
                            website = currentProfile.website,
                            instagram = currentProfile.instagram,
                            twitter = currentProfile.twitter,
                            avatarUrl = currentProfile.avatarUrl,
                            coverPhotoUrl = url,
                            createdAt = currentProfile.createdAt,
                            updatedAt = currentProfile.updatedAt
                        )
                    }
                }

                Log.d(TAG, "Cover photo uploaded successfully: $url")

            }.onFailure { error ->
                coverPhotoUploadError = error.message
                Log.e(TAG, "Cover photo upload failed", error)
            }

            isUploadingCoverPhoto = false
        }
    }


    fun saveProfile() {
        viewModelScope.launch {
            isSaving = true
            saveError = null
            saveSuccess = false

            val result = updateProfileUseCase(
                displayName = displayName,
                bio = bio,
                website = website,
                instagram = instagram,
                twitter = twitter
            )

            result.onSuccess {
                saveSuccess = true
                // Reload profile to get updated data
                loadProfile()
                Log.d(TAG, "Profile saved successfully")
            }.onFailure { error ->
                saveError = error.message
                Log.e(TAG, "Profile save failed", error)
            }

            isSaving = false
        }
    }

    fun clearErrors() {
        uploadError = null
        coverPhotoUploadError = null
        saveError = null
        profileError = null
        saveSuccess = false
    }

    // Helper to check if there are unsaved changes
    val hasUnsavedChanges: Boolean get() =
        profile?.let { p ->
            displayName != (p.displayName ?: "") ||
                    bio != (p.bio ?: "") ||
                    website != (p.website ?: "") ||
                    instagram != (p.instagram ?: "") ||
                    twitter != (p.twitter ?: "")
        } ?: (displayName.isNotEmpty() || bio.isNotEmpty() || website.isNotEmpty() ||
                instagram.isNotEmpty() || twitter.isNotEmpty())
}