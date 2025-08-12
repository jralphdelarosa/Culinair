package com.example.culinair.domain.repository

import com.example.culinair.data.remote.dto.response.LikeResponse
import com.example.culinair.data.remote.dto.response.RecipeLikeResponse
import com.example.culinair.domain.model.RecipePreviewUiModel

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
interface HomeRepository {
    suspend fun getAllPublicRecipes(userId: String, token: String): List<RecipePreviewUiModel>
    suspend fun getFollowingRecipes(userId: String, token: String): List<RecipePreviewUiModel>
    suspend fun getTrendingRecipes(userId: String, token: String): List<RecipePreviewUiModel>
    suspend fun getRecommendedRecipes(userId: String, token: String): List<RecipePreviewUiModel>
    suspend fun getRecipesByCategory(userId: String, category: String, token: String): List<RecipePreviewUiModel>
    suspend fun likeRecipe(recipeId: String, userId: String, token: String): LikeResponse?
    suspend fun saveRecipe(userId: String, recipeId: String, token: String): Boolean
    suspend fun unsaveRecipe(userId: String, recipeId: String, token: String): Boolean
}