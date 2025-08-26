package com.example.culinair.domain.repository

import com.example.culinair.data.remote.dto.response.LikeResponse
import com.example.culinair.data.remote.dto.response.SaveResponse
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.model.RecipeDetailUiModel

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
interface HomeRepository {
    suspend fun getAllPublicRecipes(userId: String, token: String): List<RecipeDetailUiModel>
    suspend fun getFollowingRecipes(userId: String, token: String): List<RecipeDetailUiModel>
    suspend fun getTrendingRecipes(userId: String, token: String): List<RecipeDetailUiModel>
    suspend fun getRecommendedRecipes(userId: String, token: String): List<RecipeDetailUiModel>
    suspend fun getRecipesByCategory(userId: String, category: String, token: String): List<RecipeDetailUiModel>
    suspend fun likeRecipe(recipeId: String, userId: String, token: String): LikeResponse?
    suspend fun saveRecipe(recipeId: String, userId: String, token: String): SaveResponse?
    suspend fun getComments(recipeId: String, token: String): List<CommentUiModel>
    suspend fun addComment(token: String, recipeId: String, content: String, parentCommentId: String? = null): CommentUiModel?
}