package com.example.culinair.domain.repository

import com.example.culinair.data.remote.dto.response.NotificationResponse

/**
* Created by John Ralph Dela Rosa on 9/2/2025.
*/
interface NotificationsRepository{
    suspend fun fetchNotifications(limit: Int = 50): Result<List<NotificationResponse>>

    suspend fun deleteNotification(id: String): Result<Boolean>

    suspend fun fetchUnreadCount(): Result<Int>

    suspend fun markNotificationAsRead(id: String): Result<Boolean>
}