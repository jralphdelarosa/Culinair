package com.example.culinair.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
data class UpdateProfileRequest(
    @SerializedName("display_name")
    val displayName: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("cover_photo_url")
    val coverPhotoUrl: String? = null,

    val bio: String? = null,
    val website: String? = null,
    val instagram: String? = null,
    val twitter: String? = null
)