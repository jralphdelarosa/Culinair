package com.example.culinair.data.remote.model.response

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
data class SupabaseAuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val user: SupabaseUser?
)
