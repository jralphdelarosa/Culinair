package com.example.culinair.data.repository

import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.NotificationApiService
import com.example.culinair.data.remote.dto.request.FCMTokenRequest
import com.example.culinair.data.remote.dto.response.FCMTokenResponse
import com.example.culinair.data.remote.dto.response.NotificationResponse
import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
class NotificationsRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: NotificationApiService
) : NotificationsRepository {

    companion object {
        private const val TAG = "NotificationsRepository"
    }

    override suspend fun fetchNotifications(limit: Int): Result<List<NotificationResponse>> {
        Log.d(TAG, "fetchNotifications called with limit: $limit")

        val token = sessionManager.getAccessToken()
        if (token == null) {
            Log.e(TAG, "fetchNotifications failed - no access token available")
            return Result.failure(Exception("No access token"))
        }

        Log.d(TAG, "Access token retrieved successfully, making API call")

        return runCatching {
            Log.d(TAG, "Calling apiService.getNotifications with limit: $limit")
            val notifications = apiService.getNotifications(token = token, limit = limit)

            Log.d(TAG, "fetchNotifications API success - received ${notifications.size} notifications")
            notifications.forEachIndexed { index, notification ->
                Log.d(TAG, "Notification $index: id=${notification.id}, type=${notification.type}, isRead=${notification.isRead}, createdAt=${notification.createdAt}")
            }

            notifications
        }.onFailure { exception ->
            Log.e(TAG, "fetchNotifications failed with exception: ${exception.message}", exception)
        }.onSuccess { notifications ->
            Log.d(TAG, "fetchNotifications completed successfully with ${notifications.size} notifications")
        }
    }

    override suspend fun deleteNotification(id: String): Result<Boolean> {
        Log.d(TAG, "deleteNotification called with id: $id")

        val token = sessionManager.getAccessToken()
        if (token == null) {
            Log.e(TAG, "deleteNotification failed - no access token available for notification id: $id")
            return Result.failure(Exception("No access token"))
        }

        Log.d(TAG, "Access token retrieved successfully, making delete API call for notification: $id")

        return runCatching {
            val filter = "eq.$id"
            Log.d(TAG, "Calling apiService.deleteNotification with filter: $filter")

            val response = apiService.deleteNotification(token = token, idEq = filter)

            Log.d(TAG, "Delete notification API response - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            if (response.isSuccessful) {
                Log.d(TAG, "Notification $id deleted successfully")
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (ex: Exception) {
                    "Error reading error body: ${ex.message}"
                }
                Log.e(TAG, "Delete notification failed for id $id with code ${response.code()}: $errorBody")
            }

            response.isSuccessful
        }.onFailure { exception ->
            Log.e(TAG, "deleteNotification failed for id $id with exception: ${exception.message}", exception)
        }.onSuccess { success ->
            Log.d(TAG, "deleteNotification completed for id $id with result: $success")
        }
    }

    override suspend fun fetchUnreadCount(): Result<Int> {
        Log.d(TAG, "fetchUnreadCount called")

        val token = sessionManager.getAccessToken()
        if (token == null) {
            Log.e(TAG, "fetchUnreadCount failed - no access token available")
            return Result.failure(Exception("No access token"))
        }

        Log.d(TAG, "Access token retrieved successfully, making unread count API call")

        return runCatching {
            Log.d(TAG, "Calling apiService.getUnreadCount")
            val response = apiService.getUnreadCount(token = token)

            Log.d(TAG, "Unread count API response received with ${response.size} items")
            response.forEachIndexed { index, countResponse ->
                Log.d(TAG, "Count response $index: count=${countResponse.count}")
            }

            val unreadCount = response.firstOrNull()?.count ?: 0
            Log.d(TAG, "Extracted unread count: $unreadCount")

            unreadCount
        }.onFailure { exception ->
            Log.e(TAG, "fetchUnreadCount failed with exception: ${exception.message}", exception)
        }.onSuccess { count ->
            Log.d(TAG, "fetchUnreadCount completed successfully with count: $count")
        }
    }

    override suspend fun markNotificationAsRead(id: String): Result<Boolean> {
        Log.d(TAG, "markNotificationAsRead called with id: $id")

        val token = sessionManager.getAccessToken()
        if (token == null) {
            Log.e(TAG, "No access token for marking notification $id as read")
            return Result.failure(Exception("No access token"))
        }

        return runCatching {
            Log.d(TAG, "Calling markAsRead API for notification: $id")
            val response = apiService.markAsRead(token, idEq = "eq.$id")

            Log.d(TAG, "Mark as read response - success: ${response.isSuccessful}, code: ${response.code()}")

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully marked notification $id as read")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                val error = "Failed to mark as read: ${response.code()} - $errorBody"
                Log.e(TAG, error)
                throw Exception(error)
            }
        }.onFailure { e ->
            Log.e(TAG, "Exception marking notification $id as read: ${e.message}", e)
        }
    }

    override suspend fun saveFcmToken(token: String, deviceId: String?): Result<Boolean> {
        Log.d(TAG, "saveFcmToken called with FCM token: ${token.take(30)}...")

        val authToken = sessionManager.getAccessToken()
        if (authToken == null) {
            Log.e(TAG, "saveFcmToken failed - no access token available")
            return Result.failure(Exception("No access token"))
        }

        return runCatching {
            val deviceInfo = deviceId ?: android.os.Build.MODEL
            val userId = sessionManager.getUserId()

            if (userId == null) {
                Log.e(TAG, "saveFcmToken failed - no user ID available")
                throw Exception("No user ID available")
            }

            Log.d(TAG, "Attempting to save FCM token for user: $userId, device: $deviceInfo")

            // Try to insert with upsert behavior
            val response = apiService.saveFcmToken(
                token = "Bearer $authToken",
                prefer = "resolution=merge-duplicates",
                tokenData = FCMTokenRequest(
                    token = token,
                    deviceId = deviceInfo,
                    userId = userId
                )
            )

            Log.d(TAG, "FCM token save response: ${response.code()}")

            when {
                response.isSuccessful -> {
                    Log.d(TAG, "FCM token saved successfully")
                    true
                }
                response.code() == 409 -> {
                    Log.d(TAG, "FCM token conflict, attempting update")
                    updateExistingFcmToken(authToken, userId, deviceInfo, token)
                }
                else -> {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (ex: Exception) {
                        "Error reading error body: ${ex.message}"
                    }
                    Log.e(TAG, "Failed to save FCM token: ${response.code()} - $errorBody")
                    throw Exception("Failed to save FCM token: HTTP ${response.code()}")
                }
            }
        }.onFailure { exception ->
            Log.e(TAG, "saveFcmToken failed with exception: ${exception.message}", exception)
        }.onSuccess { success ->
            Log.d(TAG, "saveFcmToken completed with result: $success")
        }
    }

    private suspend fun updateExistingFcmToken(
        authToken: String,
        userId: String,
        deviceId: String,
        newToken: String
    ): Boolean {
        val updateResponse = apiService.updateFcmToken(
            token = "Bearer $authToken",
            userIdEq = "eq.$userId",
            deviceIdEq = "eq.$deviceId",
            body = mapOf(
                "token" to newToken,
                "updated_at" to "now()"
            )
        )

        return if (updateResponse.isSuccessful) {
            Log.d(TAG, "FCM token updated successfully")
            true
        } else {
            val error = updateResponse.errorBody()?.string() ?: "Unknown error"
            Log.e(TAG, "Failed to update FCM token: ${updateResponse.code()} - $error")
            throw Exception("Failed to update FCM token: $error")
        }
    }


    override suspend fun deleteFcmToken(token: String): Result<Boolean> {
        Log.d(TAG, "deleteFcmToken called")

        val authToken = sessionManager.getAccessToken()
        if (authToken == null) {
            Log.e(TAG, "deleteFcmToken failed - no access token available")
            return Result.failure(Exception("No access token"))
        }

        return runCatching {
            val response = apiService.deleteFcmToken(
                token = authToken,
                tokenEq = "eq.$token"
            )

            if (response.isSuccessful) {
                Log.d(TAG, "FCM token deleted successfully")
                true
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Log.e(TAG, "Failed to delete FCM token: ${response.code()} - $error")
                throw Exception("Failed to delete FCM token: $error")
            }
        }.onFailure { exception ->
            Log.e(TAG, "deleteFcmToken failed with exception: ${exception.message}", exception)
        }
    }

    override suspend fun getUserFcmTokens(): Result<List<FCMTokenResponse>> {
        Log.d(TAG, "getUserFcmTokens called")

        val authToken = sessionManager.getAccessToken()
        if (authToken == null) {
            Log.e(TAG, "getUserFcmTokens failed - no access token available")
            return Result.failure(Exception("No access token"))
        }

        return runCatching {
            val tokens = apiService.getUserFcmTokens(token = authToken)
            Log.d(TAG, "Retrieved ${tokens.size} FCM tokens")
            tokens
        }.onFailure { exception ->
            Log.e(TAG, "getUserFcmTokens failed with exception: ${exception.message}", exception)
        }
    }
}