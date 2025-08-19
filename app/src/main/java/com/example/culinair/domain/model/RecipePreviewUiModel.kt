package com.example.culinair.domain.model

import com.example.culinair.data.remote.dto.response.HomeRecipeResponse

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
data class RecipePreviewUiModel(
    val id: String,
    val title: String,
    val imageUrl: String? = "",
    val displayName: String,
    val avatarUrl: String? = "",
    val likes: Int,
    val saves: Int,
    val comments: Int,
    val category: String,
    val cookTimeMinutes: Int,
    val difficulty: String,
    val isLiked: Boolean = false,
    val likesCount: Int,
    // Check if current user has liked this recipe
    val isLikedByCurrentUser: Boolean = false,
    val isSavedByCurrentUser: Boolean = false

)

fun HomeRecipeResponse.toPreviewUi(currentUserId: String): RecipePreviewUiModel {
    return RecipePreviewUiModel(
        id = id,
        title = title,
        imageUrl = imageUrl,
        displayName = userProfile?.displayName ?: "Unknown",
        avatarUrl = userProfile?.avatarUrl,
        likes = likesCount,
        saves = savesCount,
        comments = commentsCount,
        category = category,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = difficulty,
        isLiked = isLiked,
        likesCount = likesCount,
        // Check if current user has liked this recipe
        isLikedByCurrentUser = recipeLikes?.any { it.userId == currentUserId } == true,
        isSavedByCurrentUser = recipeSaves?.any { it.userId == currentUserId } == true
    )
}
