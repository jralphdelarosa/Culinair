package com.example.culinair.domain.usecase.notifications

import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
@Singleton
class DeleteFcmTokenUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(token: String): Result<Boolean> {
        return notificationsRepository.deleteFcmToken(token)
    }
}