package com.example.culinair.data.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
enum class NotificationType { FOLLOW, LIKE, COMMENT, SAVE, SYSTEM }

data class NotificationResponse(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("actor_id")
    val actorId: String?,
    val type: String,
    @SerializedName("recipe_id")// map to NotificationType safely
    val recipeId: String?,
    val message: String?,
    @SerializedName("is_read")
    val isRead: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)