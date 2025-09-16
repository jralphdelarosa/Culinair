package com.example.culinair.domain.usecase.notifications

import com.example.culinair.data.remote.dto.response.FCMTokenResponse
import com.example.culinair.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
@Singleton
class GetUserFcmTokensUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(): Result<List<FCMTokenResponse>> {
        return notificationsRepository.getUserFcmTokens()
    }
}