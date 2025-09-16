package com.example.culinair.data.remote.model.response

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
data class FCMTokenResponse(
    val id: String,
    val token: String,
    val device_id: String?,
    val created_at: String
)