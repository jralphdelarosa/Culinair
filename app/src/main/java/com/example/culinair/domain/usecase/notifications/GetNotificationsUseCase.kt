package com.example.culinair.domain.usecase.notifications

import com.example.culinair.data.remote.model.response.NotificationResponse
import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(limit: Int = 50): Result<List<NotificationResponse>> {
        return repository.fetchNotifications(limit)
    }
}