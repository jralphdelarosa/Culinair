package com.example.culinair.domain.usecase.notifications

import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
@Singleton
class SaveFcmTokenUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(token: String, deviceId: String? = null): Result<Boolean> {
        return notificationsRepository.saveFcmToken(token, deviceId)
    }
}