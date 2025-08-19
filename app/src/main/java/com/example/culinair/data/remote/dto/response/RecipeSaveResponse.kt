package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/20/2025.
 */
data class RecipeSaveResponse(
    @SerializedName("user_id")
    val userId: String
)