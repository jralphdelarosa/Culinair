package com.example.culinair.data.remote.model.response

import com.example.culinair.domain.model.AuthMethod
import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */

data class UserSession(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    val authMethod: AuthMethod = AuthMethod.EmailPassword,
    val userId: String? // You'll need to extract the ID from this
)