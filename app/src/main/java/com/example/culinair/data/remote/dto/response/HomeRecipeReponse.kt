package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
// API Response Models
data class RecipeResponse(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
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
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("is_public")
    val isPublic: Boolean,
    @SerializedName("likes_count")
    val likesCount: Int,
    @SerializedName("comments_count")
    val commentsCount: Int,
    @SerializedName("saves_count")
    val savesCount: Int,
    @SerializedName("is_liked")
    val isLiked: Boolean,
    // Embedded user profile from Supabase join
    @SerializedName("user_profiles")
    val userProfile: UserProfileMini?,
    // This will contain the current user's like record if they liked it
    @SerializedName("recipe_likes")
    val recipeLikes: List<RecipeLikeResponse>? = null,
    // This will contain the current user's save record if they liked it
    @SerializedName("recipe_saves")
    val recipeSaves: List<RecipeSaveResponse>? = null
)
