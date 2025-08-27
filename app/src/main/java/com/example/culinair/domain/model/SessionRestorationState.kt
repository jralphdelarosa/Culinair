package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.UserSession

/**
 * Created by John Ralph Dela Rosa on 8/28/2025.
 */
sealed class SessionRestorationState {
    object Loading : SessionRestorationState()
    object NoSession : SessionRestorationState()
    data class Success(val session: UserSession) : SessionRestorationState()
    data class Error(val exception: Throwable) : SessionRestorationState()
}