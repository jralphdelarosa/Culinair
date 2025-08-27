package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */

data class ProfileResponse(
    val id: String,

    @SerializedName("display_name")
    val displayName: String?,

    val bio: String?,
    val website: String?,
    val instagram: String?,
    val twitter: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("cover_photo_url")
    val coverPhotoUrl: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)