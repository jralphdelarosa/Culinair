package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/11/2025.
 */
data class LikeResponse(
    val liked: Boolean,
    @SerializedName("likes_count")
    val likesCount: Int
)