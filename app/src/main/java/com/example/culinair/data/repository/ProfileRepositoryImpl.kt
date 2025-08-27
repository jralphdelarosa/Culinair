package com.example.culinair.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.ProfileApiService
import com.example.culinair.data.remote.dto.request.UpdateProfileRequest
import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val profileApiService: ProfileApiService
) : ProfileRepository {

    companion object {
        private const val TAG = "ProfileRepository" +
                ""
    }

    private val avatarBaseStorageUrl = "https://voygyldtkkbwdljnfwfg.supabase.co/storage/v1/object/public/avatars/"
    private val coverPhotoBaseStorageUrl = "https://voygyldtkkbwdljnfwfg.supabase.co/storage/v1/object/public/cover-photo/"

    override suspend fun uploadAvatarRaw(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== UPLOAD AVATAR DEBUG ===")
            Log.d(TAG, "URI: $uri")

            val userId = sessionManager.getUserId() ?: return@withContext Result.failure(Exception("Not authenticated"))
            val token = sessionManager.getAccessToken() ?: return@withContext Result.failure(Exception("No access token"))

            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Token present: ${token.isNotBlank()}")

            val fileName = "avatar.jpg"

            Log.d(TAG, "Generated filename: $fileName")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Failed to read image"))

            val bytes = inputStream.readBytes()
            Log.d(TAG, "Image size: ${bytes.size} bytes (${bytes.size / 1024}KB)")

            if (bytes.size > 5 * 1024 * 1024) {
                Log.e(TAG, "❌ Image too large: ${bytes.size} bytes > 5MB")
                return@withContext Result.failure(Exception("Image too large. Max size is 5MB."))
            }

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

            Log.d(TAG, "User ID path: $userId")
            Log.d(TAG, "Filename path: $fileName")
            Log.d(TAG, "Full expected path: avatars/$userId/$fileName")

            val response = profileApiService.uploadAvatarRaw(
                userId = userId,
                fileName = fileName,
                auth = "Bearer $token",
                image = requestFile // This is your image request body
            )

            Log.d(TAG, "=== UPLOAD RESPONSE ===")
            Log.d(TAG, "Status: ${response.code()}")
            Log.d(TAG, "Headers:")
            response.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            val uploadResponseBody = response.body()?.string()
            Log.d(TAG, "Response body: $uploadResponseBody")

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Avatar upload failed - Status: ${response.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Avatar upload failed: ${response.code()} - $errorBody"))
            }

            val timestamp = System.currentTimeMillis()
            val avatarUrl = "$avatarBaseStorageUrl$userId/$fileName?v=$timestamp"

            Log.d(TAG, "✅ Upload successful - Avatar URL: $avatarUrl")

            Log.d(TAG, "=== UPDATING PROFILE WITH AVATAR URL ===")

            // Update profile with new avatar URL
            val updateResponse = profileApiService.updateProfile(
                id = "eq.$userId",
                auth = "Bearer $token",
                updateRequest = UpdateProfileRequest( // skip or prefill with current if needed
                    avatarUrl = avatarUrl
                )
            )

            Log.d(TAG, "=== PROFILE UPDATE RESPONSE ===")
            Log.d(TAG, "Status: ${updateResponse.code()}")
            Log.d(TAG, "Headers:")
            updateResponse.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            val updateResponseBody = updateResponse.body()?.toString()
            Log.d(TAG, "Update response body: $updateResponseBody")

            if (!updateResponse.isSuccessful) {
                val errorBody = updateResponse.errorBody()?.string()
                Log.e(TAG, "❌ Avatar URL update failed - Status: ${updateResponse.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Avatar URL update failed: ${updateResponse.code()} - $errorBody"))
            }

            Log.d(TAG, "✅ Avatar upload and profile update successful!")
            Result.success(avatarUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Avatar upload exception", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadCoverPhotoRaw(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== UPLOAD COVER PHOTO DEBUG ===")
            Log.d(TAG, "URI: $uri")

            val userId = sessionManager.getUserId() ?: return@withContext Result.failure(Exception("Not authenticated"))
            val token = sessionManager.getAccessToken() ?: return@withContext Result.failure(Exception("No access token"))

            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Token present: ${token.isNotBlank()}")

            val fileName = "cover.jpg"

            Log.d(TAG, "Generated filename: $fileName")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Failed to read image"))

            val bytes = inputStream.readBytes()
            Log.d(TAG, "Image size: ${bytes.size} bytes (${bytes.size / 1024}KB)")

            if (bytes.size > 10 * 1024 * 1024) { // Allow larger size for cover photos
                Log.e(TAG, "❌ Image too large: ${bytes.size} bytes > 10MB")
                return@withContext Result.failure(Exception("Image too large. Max size is 10MB."))
            }

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

            Log.d(TAG, "User ID path: $userId")
            Log.d(TAG, "Filename path: $fileName")
            Log.d(TAG, "Full expected path: cover-photo/$userId/$fileName")

            val response = profileApiService.uploadCoverPhotoRaw(
                userId = userId,
                fileName = fileName,
                auth = "Bearer $token",
                image = requestFile
            )

            Log.d(TAG, "=== UPLOAD RESPONSE ===")
            Log.d(TAG, "Status: ${response.code()}")
            Log.d(TAG, "Headers:")
            response.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            val uploadResponseBody = response.body()?.string()
            Log.d(TAG, "Response body: $uploadResponseBody")

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Cover photo upload failed - Status: ${response.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Cover photo upload failed: ${response.code()} - $errorBody"))
            }

            val timestamp = System.currentTimeMillis()
            val coverPhotoUrl = "$coverPhotoBaseStorageUrl$userId/$fileName?v=$timestamp"

            Log.d(TAG, "✅ Upload successful - Cover Photo URL: $coverPhotoUrl")

            Log.d(TAG, "=== UPDATING PROFILE WITH COVER PHOTO URL ===")

            // Update profile with new cover photo URL
            val updateResponse = profileApiService.updateProfile(
                id = "eq.$userId",
                auth = "Bearer $token",
                updateRequest = UpdateProfileRequest(
                    coverPhotoUrl = coverPhotoUrl
                )
            )

            Log.d(TAG, "=== PROFILE UPDATE RESPONSE ===")
            Log.d(TAG, "Status: ${updateResponse.code()}")
            Log.d(TAG, "Headers:")
            updateResponse.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            val updateResponseBody = updateResponse.body()?.toString()
            Log.d(TAG, "Update response body: $updateResponseBody")

            if (!updateResponse.isSuccessful) {
                val errorBody = updateResponse.errorBody()?.string()
                Log.e(TAG, "❌ Cover photo URL update failed - Status: ${updateResponse.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Cover photo URL update failed: ${updateResponse.code()} - $errorBody"))
            }

            Log.d(TAG, "✅ Cover photo upload and profile update successful!")
            Result.success(coverPhotoUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Cover photo upload exception", e)
            Result.failure(e)
        }
    }

    override suspend fun updateProfileInfo(
        displayName: String,
        bio: String,
        website: String,
        instagram: String,
        twitter: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== UPDATE PROFILE INFO DEBUG ===")
            Log.d(TAG, "Display name: '$displayName'")
            Log.d(TAG, "Bio: '$bio'")
            Log.d(TAG, "Website: '$website'")
            Log.d(TAG, "Instagram: '$instagram'")
            Log.d(TAG, "Twitter: '$twitter'")

            val userId = sessionManager.getUserId() ?: return@withContext Result.failure(Exception("User not authenticated"))
            val token = sessionManager.getAccessToken() ?: return@withContext Result.failure(Exception("No token"))

            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Token present: ${token.isNotBlank()}")

            val request = UpdateProfileRequest(
                displayName = displayName,
                avatarUrl = null,
                bio = bio,
                website = website,
                instagram = instagram,
                twitter = twitter
            )

            Log.d(TAG, "=== SENDING UPDATE REQUEST ===")
            Log.d(TAG, "Update request: $request")

            val response = profileApiService.updateProfile("eq.$userId", "Bearer $token", request)

            Log.d(TAG, "=== UPDATE RESPONSE ===")
            Log.d(TAG, "Status: ${response.code()}")
            Log.d(TAG, "Headers:")
            response.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            val responseBody = response.body()?.toString()
            Log.d(TAG, "Response body: $responseBody")

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Profile update failed - Status: ${response.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Profile update failed: ${response.code()} - $errorBody"))
            }

            Log.d(TAG, "✅ Profile update successful!")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Profile update exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== GET PROFILE DEBUG ===")

            val userId = sessionManager.getUserId() ?: return@withContext Result.failure(Exception("Not authenticated"))
            val token = sessionManager.getAccessToken() ?: return@withContext Result.failure(Exception("No access token"))

            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Token present: ${token.isNotBlank()}")
            Log.d(TAG, "Query filter: eq.$userId")

            Log.d(TAG, "=== FETCHING PROFILE ===")

            val response = profileApiService.getProfile("eq.$userId", "Bearer $token")

            Log.d(TAG, "=== GET PROFILE RESPONSE ===")
            Log.d(TAG, "Status: ${response.code()}")
            Log.d(TAG, "Headers:")
            response.headers().forEach { header ->
                Log.d(TAG, "  ${header.first}: ${header.second}")
            }

            if (response.isSuccessful) {
                val profiles = response.body().orEmpty()
                Log.d(TAG, "Raw response body: $profiles")
                Log.d(TAG, "Number of profiles returned: ${profiles.size}")

                if (profiles.isNotEmpty()) {
                    val profile = profiles.first()
                    Log.d(TAG, "=== PROFILE DETAILS ===")
                    Log.d(TAG, "ID: ${profile.id}")
                    Log.d(TAG, "Display Name: '${profile.displayName}'")
                    Log.d(TAG, "Bio: '${profile.bio}'")
                    Log.d(TAG, "Avatar URL: '${profile.avatarUrl}'")
                    Log.d(TAG, "Cover Photo URL: '${profile.coverPhotoUrl}'")
                    Log.d(TAG, "Website: '${profile.website}'")
                    Log.d(TAG, "Instagram: '${profile.instagram}'")
                    Log.d(TAG, "Twitter: '${profile.twitter}'")
                    Log.d(TAG, "Created At: '${profile.createdAt}'")
                    Log.d(TAG, "Updated At: '${profile.updatedAt}'")
                    Log.d(TAG, "✅ Profile fetched successfully")
                    Result.success(profile)
                } else {
                    Log.e(TAG, "❌ No profile found for user ID: $userId")
                    Log.e(TAG, "This usually means:")
                    Log.e(TAG, "1. User profile doesn't exist in database")
                    Log.e(TAG, "2. RLS policy is blocking the query")
                    Log.e(TAG, "3. Filter syntax is wrong")
                    Result.failure(Exception("No profile found for user ID"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Profile fetch failed - Status: ${response.code()}, Error: $errorBody")
                Result.failure(Exception("Failed to fetch profile: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Get profile exception", e)
            Result.failure(e)
        }
    }
}
