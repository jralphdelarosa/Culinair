package com.example.culinair.data.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/11/2025.
 */
data class RecipeLikeResponse(
    @SerializedName("user_id")
    val userId: String
)