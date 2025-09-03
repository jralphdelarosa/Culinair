package com.example.culinair.data.repository

import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.NotificationApiService
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
}