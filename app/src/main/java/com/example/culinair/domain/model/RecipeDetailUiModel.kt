package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.RecipeResponse

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
data class RecipeDetailUiModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val displayName: String,
    val avatarUrl: String?,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val category: String,
    val tags: List<String>,
    val cookTimeMinutes: Int,
    val difficulty: String,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val savesCount: Int,
    val isLikedByCurrentUser: Boolean,
    val isSavedByCurrentUser: Boolean
)

fun RecipeResponse.toPreviewUi(currentUserId: String): RecipeDetailUiModel {
    return RecipeDetailUiModel(
        id = id,
        title = title,
        imageUrl = imageUrl,
        displayName = userProfile?.displayName ?: "Unknown",
        avatarUrl = userProfile?.avatarUrl,
        description = description,
        ingredients = ingredients,
        steps = steps,
        category = category,
        tags = tags,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = difficulty,
        createdAt = createdAt,
        likesCount = likesCount,
        commentsCount = commentsCount,
        savesCount = savesCount,
        isLikedByCurrentUser = recipeLikes?.any { it.userId == currentUserId } == true,
        isSavedByCurrentUser = recipeSaves?.any { it.userId == currentUserId } == true
    )
}
