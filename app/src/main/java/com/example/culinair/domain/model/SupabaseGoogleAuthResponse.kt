package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.SupabaseUser

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
data class SupabaseGoogleAuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val user: SupabaseUser?
)