package com.example.culinair.domain.model

/**
 * Created by John Ralph Dela Rosa on 9/3/2025.
 */
data class NotificationUIModel(
    val id: String,
    val actorId: String,
    val actorName: String,
    val actorAvatar: String?,
    val recipeId: String?,
    val message: String,
    val type: String,
    val createdAt: String,
    val isRead: Boolean
)