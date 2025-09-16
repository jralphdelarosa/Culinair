package com.example.culinair.data.remote.model.request

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
data class SaveRecipeRequest(
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("user_id")
    val userId: String
)