package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.CommentResponse

/**
 * Created by John Ralph Dela Rosa on 8/22/2025.
 */
data class CommentUiModel(
    val id: String,
    val recipeId: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val content: String,
    val createdAt: String,
    val parentId: String? = null,
    val children: List<CommentUiModel> = emptyList()
)

fun CommentResponse.toUiModel(): CommentUiModel {
    return CommentUiModel(
        id = id,
        recipeId = recipeId,
        userId = userId,
        displayName = userProfile?.displayName ?: "Unknown",
        avatarUrl = userProfile?.avatarUrl,
        content = content,
        createdAt = createdAt,
        parentId = parentCommentId, // Add this!
        children = emptyList()
    )
}