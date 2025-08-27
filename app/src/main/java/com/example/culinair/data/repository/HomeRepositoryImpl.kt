package com.example.culinair.data.repository

import android.util.Log
import com.example.culinair.data.remote.apiservice.HomeApiService
import com.example.culinair.data.remote.dto.request.CommentRequest
import com.example.culinair.data.remote.dto.response.AddCommentResponse
import com.example.culinair.data.remote.dto.response.CommentResponse
import com.example.culinair.data.remote.dto.response.LikeResponse
import com.example.culinair.data.remote.dto.response.SaveResponse
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.model.RecipeDetailUiModel
import com.example.culinair.domain.model.toPreviewUi
import com.example.culinair.domain.model.toUiModel
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

    override suspend fun getAllPublicRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
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

    override suspend fun getFollowingRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
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

    override suspend fun getTrendingRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
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

    override suspend fun getRecommendedRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
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

    override suspend fun getRecipesByCategory(userId: String, category: String, token: String): List<RecipeDetailUiModel> {
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

    override suspend fun saveRecipe(recipeId: String, userId: String, token: String): SaveResponse? {
        return try {
            val response = service.saveRecipe(
                token = "Bearer $token",
                body = mapOf("recipe_id" to recipeId, "user_id" to userId)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "saveRecipe exception: ${e.message}", e)
            null
        }
    }

    override suspend fun getComments(recipeId: String, token: String): List<CommentUiModel> {
        return try {
            val response = service.getCommentsForRecipe(
                token = "Bearer $token",
                recipeId = "eq.$recipeId"
            )

            // Add logging to see what server returns
            Log.d(TAG, "Server returned ${response.size} comments")
            response.forEach { comment ->
                Log.d(TAG, "Comment: id=${comment.id}, userId=${comment.userId}, content=${comment.content}")
            }

            buildNestedComments(response)
        } catch (e: Exception) {
            Log.e(TAG, "getComments exception: ${e.message}", e)
            emptyList()
        }
    }

    private fun buildNestedComments(flatList: List<CommentResponse>): List<CommentUiModel> {
        if (flatList.isEmpty()) return emptyList()

        // Step 1: Convert all to UI models
        val allComments = mutableMapOf<String, CommentUiModel>()

        flatList.forEach { comment ->
            val uiComment = CommentUiModel(
                id = comment.id,
                recipeId = comment.recipeId,
                userId = comment.userId,
                displayName = comment.userProfile?.displayName ?: "Unknown",
                avatarUrl = comment.userProfile?.avatarUrl,
                content = comment.content,
                createdAt = comment.createdAt,
                parentId = comment.parentCommentId,
                children = emptyList()
            )
            allComments[comment.id] = uiComment
        }

        // Step 2: Find root comments and direct replies only
        val rootComments = mutableListOf<CommentUiModel>()
        val repliesMap = mutableMapOf<String, MutableList<CommentUiModel>>()

        flatList.forEach { comment ->
            val uiComment = allComments[comment.id]!!

            if (comment.parentCommentId == null) {
                // This is a root comment
                rootComments.add(uiComment)
            } else {
                // This is a reply - find the root parent
                val rootParentId = findRootParent(flatList, comment.id)
                if (rootParentId != null) {
                    repliesMap.getOrPut(rootParentId) { mutableListOf() }.add(uiComment)
                }
            }
        }

        // Step 3: Attach all replies to root comments (flattened)
        val result = rootComments.map { rootComment ->
            val allReplies = repliesMap[rootComment.id] ?: emptyList()
            val sortedReplies = allReplies.sortedBy { it.createdAt }
            rootComment.copy(children = sortedReplies)
        }.sortedBy { it.createdAt }

        Log.d(TAG, "Built flattened comment tree: ${result.size} root comments")
        result.forEach { rootComment ->
            Log.d(TAG, "Root comment ${rootComment.id} has ${rootComment.children.size} total replies")
        }

        return result
    }

    // Helper to find the root parent of any comment
    private fun findRootParent(flatList: List<CommentResponse>, commentId: String): String? {
        val comment = flatList.find { it.id == commentId } ?: return null

        return if (comment.parentCommentId == null) {
            // This is already a root comment
            comment.id
        } else {
            // Find the root of the parent
            findRootParent(flatList, comment.parentCommentId)
        }
    }

    override suspend fun addCommentAndUpdateCount(
        token: String,
        recipeId: String,
        userId: String,
        content: String,
        parentCommentId: String?
    ): AddCommentResponse? {
        return try {
            val response = service.addCommentAndUpdateCount(
                token = "Bearer $token",
                body = mapOf(
                    "recipe_id" to recipeId,
                    "user_id" to userId,
                    "content" to content,
                    "parent_comment_id" to parentCommentId
                )
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "addCommentAndUpdateCount exception: ${e.message}", e)
            null
        }
    }

}
