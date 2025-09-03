package com.example.culinair.domain.usecase.notifications

import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return repository.deleteNotification(id)
    }
}