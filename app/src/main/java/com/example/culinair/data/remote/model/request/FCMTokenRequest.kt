package com.example.culinair.data.remote.model.request

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
data class FCMTokenRequest(
    val token: String,
    @SerializedName("device_id")
    val deviceId: String? = null,
    @SerializedName("user_id")
    val userId: String? = null
)