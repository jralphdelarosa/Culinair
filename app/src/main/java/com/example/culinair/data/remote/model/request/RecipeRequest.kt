package com.example.culinair.data.remote.model.request

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
data class CreateRecipeRequest(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    @SerializedName("image_url")
    val imageUrl: String,
    val category: String,
    val tags: List<String>,
    @SerializedName("cook_time_minutes")
    val cookTimeMinutes: Int,
    val difficulty: String,
    @SerializedName("user_id")
    val userId: String
)