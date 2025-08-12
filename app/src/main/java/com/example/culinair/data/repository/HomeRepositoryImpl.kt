package com.example.culinair.data.repository

import android.util.Log
import com.example.culinair.data.remote.apiservice.HomeApiService
import com.example.culinair.data.remote.dto.request.SaveRecipeRequest
import com.example.culinair.data.remote.dto.response.LikeResponse
import com.example.culinair.data.remote.dto.response.RecipeLikeResponse
import com.example.culinair.domain.model.RecipePreviewUiModel
import com.example.culinair.domain.model.toPreviewUi
import com.example.culinair.domain.repository.HomeRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val service: HomeApiService
) : HomeRepository {

    companion object {
        private const val TAG = "HomeRepository"
    }

    override suspend fun getAllPublicRecipes(userId: String, token: String): List<RecipePreviewUiModel> {
        return try {
            val result = service.getAllPublicRecipes(token = "Bearer $token")
            Log.d(TAG, "getAllPublicRecipes success: ${result.size} recipes")
            Log.d(TAG, "==RECIPES FOR ALL PUBLIC==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }
            result.map { it.toPreviewUi(userId) }
        } catch (e: HttpException) {
            Log.e(TAG, "getAllPublicRecipes failed: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getAllPublicRecipes exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getFollowingRecipes(userId: String, token: String): List<RecipePreviewUiModel> {
        return try {
            val result = service.getFollowingRecipes(
                token = "Bearer $token",
                userId = "eq.$userId"
            )
            Log.d(TAG, "getFollowingRecipes success: ${result.size} recipes")
            Log.d(TAG, "==RECIPES FOR FOLLOWING==")
            result.forEach{ recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }
            result.map { it.toPreviewUi(userId) }
        } catch (e: HttpException) {
            Log.e(TAG, "getFollowingRecipes failed: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getFollowingRecipes exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getTrendingRecipes(userId: String, token: String): List<RecipePreviewUiModel> {
        return try {
            val result = service.getTrendingRecipes(token = "Bearer $token")
            Log.d(TAG, "getTrendingRecipes success: ${result.size} recipes")
            Log.d(TAG, "==RECIPES FOR TRENDING==")
            result.forEach{ recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }
            result.map { it.toPreviewUi(userId) }
        } catch (e: HttpException) {
            Log.e(TAG, "getTrendingRecipes failed: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getTrendingRecipes exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecommendedRecipes(userId: String, token: String): List<RecipePreviewUiModel> {
        return try {
            val result = service.getRecommendedRecipes(token = "Bearer $token")
            Log.d(TAG, "getRecommendedRecipes success: ${result.size} recipes")
            Log.d(TAG, "==RECIPES FOR RECOMMENDED==")
            result.forEach{ recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }
            result.map { it.toPreviewUi(userId) }
        } catch (e: HttpException) {
            Log.e(TAG, "getRecommendedRecipes failed: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getRecommendedRecipes exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipesByCategory(userId: String, category: String, token: String): List<RecipePreviewUiModel> {
        return try {
            val result = service.getRecipesByCategory(
                token = "Bearer $token",
                category = "eq.$category"
            )
            Log.d(TAG, "getRecipesByCategory success: ${result.size} recipes")
            result.map { it.toPreviewUi(userId) }
        } catch (e: HttpException) {
            Log.e(TAG, "getRecipesByCategory failed: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getRecipesByCategory exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun likeRecipe(recipeId: String, userId: String, token: String): LikeResponse? {
        return try {
            val response = service.likeRecipe(
                token = "Bearer $token",
                body = mapOf("recipe_id" to recipeId, "user_id" to userId)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "likeRecipe exception: ${e.message}", e)
            null
        }
    }

    override suspend fun saveRecipe(userId: String, recipeId: String, token: String): Boolean {
        return try {
            val response = service.saveRecipe("Bearer $token", SaveRecipeRequest(userId, recipeId))
            if (!response.isSuccessful) {
                Log.e(TAG, "saveRecipe failed: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: HttpException) {
            Log.e(TAG, "saveRecipe exception: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "saveRecipe exception: ${e.message}", e)
            false
        }
    }

    override suspend fun unsaveRecipe(userId: String, recipeId: String, token: String): Boolean {
        return try {
            val response = service.unsaveRecipe("Bearer $token", userId = "eq.$userId", recipeId = "eq.$recipeId")
            if (!response.isSuccessful) {
                Log.e(TAG, "unsaveRecipe failed: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: HttpException) {
            Log.e(TAG, "unsaveRecipe exception: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "unsaveRecipe exception: ${e.message}", e)
            false
        }
    }
}
