package com.example.culinair.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/26/2025.
 */
data class CommentResponse(
    val id: String,
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("user_id")
    val userId: String,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("parent_comment_id")
    val parentCommentId: String?,
    @SerializedName("user_profiles")
    val userProfile: UserProfileMini? // for avatar & name
)