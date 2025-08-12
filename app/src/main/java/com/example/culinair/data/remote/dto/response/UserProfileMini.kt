package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
data class UserProfileMini(
    val id: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)