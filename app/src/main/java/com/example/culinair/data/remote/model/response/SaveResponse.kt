package com.example.culinair.data.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/20/2025.
 */
data class SaveResponse(
    val saved: Boolean,
    @SerializedName("saves_count")
    val savesCount: Int
)