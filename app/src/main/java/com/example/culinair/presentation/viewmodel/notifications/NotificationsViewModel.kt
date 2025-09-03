package com.example.culinair.presentation.viewmodel.notifications

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.domain.model.NotificationUIModel
import com.example.culinair.domain.usecase.notifications.DeleteNotificationUseCase
import com.example.culinair.domain.usecase.notifications.GetNotificationsUseCase
import com.example.culinair.domain.usecase.notifications.GetUnreadCountUseCase
import com.example.culinair.domain.usecase.notifications.MarkNotificationAsReadUseCase
import com.example.culinair.domain.usecase.profile.GetProfileByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    var notifications by mutableStateOf<List<NotificationUIModel>>(emptyList())
        private set

    var unreadCount by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadNotifications()
    }

    fun loadNotifications(limit: Int = 50) {
        Log.d(TAG, "loadNotifications called with limit: $limit")

        viewModelScope.launch {
            Log.d(TAG, "Starting notifications loading process")
            isLoading = true

            // Fetch notifications first
            Log.d(TAG, "Fetching notifications with limit: $limit")
            val notificationsResult = getNotificationsUseCase(limit)

            notificationsResult
                .onSuccess { fetchedNotifications ->
                    Log.d(TAG, "Successfully fetched ${fetchedNotifications.size} notifications")

                    // Fetch profiles for all actors in parallel
                    Log.d(TAG, "Starting parallel profile fetching for ${fetchedNotifications.size} actors")
                    val enrichedNotifications = fetchedNotifications.map { notif ->
                        async {
                            Log.d(TAG, "Fetching profile for actor: ${notif.actorId}")
                            val profileResult = getProfileByIdUseCase(notif.actorId ?: "")

                            profileResult.fold(
                                onSuccess = { profile ->
                                    Log.d(TAG, "Profile loaded for actor ${notif.actorId}: ${profile.displayName}")
                                    NotificationUIModel(
                                        id = notif.id,
                                        actorId = notif.actorId ?: "",
                                        actorName = profile.displayName ?: "Unknown",
                                        actorAvatar = profile.avatarUrl,
                                        recipeId = notif.recipeId,
                                        message = notif.message ?: "",
                                        type = notif.type,
                                        createdAt = notif.createdAt,
                                        isRead = notif.isRead
                                    )
                                },
                                onFailure = { e ->
                                    Log.w(TAG, "Failed to load profile for actor ${notif.actorId}: ${e.message}")
                                    // Fallback if profile fetch fails
                                    NotificationUIModel(
                                        id = notif.id,
                                        actorId = notif.actorId ?: "",
                                        actorName = "Unknown",
                                        actorAvatar = null,
                                        recipeId = notif.recipeId,
                                        message = notif.message ?: "",
                                        type = notif.type,
                                        createdAt = notif.createdAt,
                                        isRead = notif.isRead
                                    )
                                }
                            )
                        }
                    }.awaitAll() // Wait for all profile fetches to finish

                    Log.d(TAG, "All profile fetching completed, updating notifications list")
                    notifications = enrichedNotifications
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to fetch notifications: ${e.message}", e)
                }

            // Also update unread count
            Log.d(TAG, "Fetching unread count")
            getUnreadCountUseCase()
                .onSuccess { count ->
                    Log.d(TAG, "Successfully loaded unread count: $count")
                    unreadCount = count
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to load unread count: ${e.message}", e)
                }

            Log.d(TAG, "Notifications loading process completed")
            isLoading = false
        }
    }

    fun markAsRead(id: String) {
        Log.d(TAG, "markAsRead called for notification: $id")

        viewModelScope.launch {

            Log.d(TAG, "Marking notification $id as read")

            markNotificationAsReadUseCase(id)
                .onSuccess { success ->

                    Log.d(TAG, "Mark as read result for $id: $success")

                    if (success) {

                        Log.d(TAG, "Updating local notification state for $id")
                        // Update local list
                        notifications = notifications.map { notif ->
                            if (notif.id == id) notif.copy(isRead = true) else notif
                        }

                        // ✅ Optimistically decrement unread count
                        unreadCount = (unreadCount - 1).coerceAtLeast(0)

                        Log.d(TAG, "Refreshing unread count after marking $id as read")
                        // ✅ Sync with backend after a short delay (so DB is updated)
                        delay(500)
                        refreshUnreadCount()
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Error marking notification $id as read: ${e.message}", e)
                }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            deleteNotificationUseCase(id)
                .onSuccess { success ->
                    if (success) {
                        notifications = notifications.filterNot { it.id == id }
                        refreshUnreadCount()
                    }
                }
                .onFailure {
                    // handle error
                }
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            getUnreadCountUseCase()
                .onSuccess {
                    unreadCount = it
                }
        }
    }
}