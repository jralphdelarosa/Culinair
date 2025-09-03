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
        Log.d(TAG, "getAllPublicRecipes called with userId: $userId")
        return try {
            Log.d(TAG, "Making API call to get all public recipes")
            val result = service.getAllPublicRecipes(token = "Bearer $token")
            Log.d(TAG, "getAllPublicRecipes API success: ${result.size} recipes received")

            Log.d(TAG, "==RECIPES FOR ALL PUBLIC==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }

            Log.d(TAG, "Converting ${result.size} recipes to UI models for userId: $userId")
            val uiModels = result.map { it.toPreviewUi(userId) }
            Log.d(TAG, "Successfully converted to ${uiModels.size} UI models")
            uiModels
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Error reading error body: ${ex.message}"
            }
            Log.e(TAG, "getAllPublicRecipes HTTP error: ${e.code()} - $errorBody")
            Log.e(TAG, "HTTP exception details", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getAllPublicRecipes unexpected exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getFollowingRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
        Log.d(TAG, "getFollowingRecipes called with userId: $userId")
        return try {
            Log.d(TAG, "Making API call to get following recipes with filter: eq.$userId")
            val result = service.getFollowingRecipes(
                token = "Bearer $token",
                userId = "eq.$userId"
            )
            Log.d(TAG, "getFollowingRecipes API success: ${result.size} recipes received")

            Log.d(TAG, "==RECIPES FOR FOLLOWING==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }

            Log.d(TAG, "Converting ${result.size} following recipes to UI models")
            val uiModels = result.map { it.toPreviewUi(userId) }
            Log.d(TAG, "Successfully converted to ${uiModels.size} following UI models")
            uiModels
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Error reading error body: ${ex.message}"
            }
            Log.e(TAG, "getFollowingRecipes HTTP error: ${e.code()} - $errorBody")
            Log.e(TAG, "HTTP exception details", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getFollowingRecipes unexpected exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getTrendingRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
        Log.d(TAG, "getTrendingRecipes called with userId: $userId")
        return try {
            Log.d(TAG, "Making API call to get trending recipes")
            val result = service.getTrendingRecipes(token = "Bearer $token")
            Log.d(TAG, "getTrendingRecipes API success: ${result.size} recipes received")

            Log.d(TAG, "==RECIPES FOR TRENDING==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }

            Log.d(TAG, "Converting ${result.size} trending recipes to UI models")
            val uiModels = result.map { it.toPreviewUi(userId) }
            Log.d(TAG, "Successfully converted to ${uiModels.size} trending UI models")
            uiModels
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Error reading error body: ${ex.message}"
            }
            Log.e(TAG, "getTrendingRecipes HTTP error: ${e.code()} - $errorBody")
            Log.e(TAG, "HTTP exception details", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getTrendingRecipes unexpected exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecommendedRecipes(userId: String, token: String): List<RecipeDetailUiModel> {
        Log.d(TAG, "getRecommendedRecipes called with userId: $userId")
        return try {
            Log.d(TAG, "Making API call to get recommended recipes")
            val result = service.getRecommendedRecipes(token = "Bearer $token")
            Log.d(TAG, "getRecommendedRecipes API success: ${result.size} recipes received")

            Log.d(TAG, "==RECIPES FOR RECOMMENDED==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }

            Log.d(TAG, "Converting ${result.size} recommended recipes to UI models")
            val uiModels = result.map { it.toPreviewUi(userId) }
            Log.d(TAG, "Successfully converted to ${uiModels.size} recommended UI models")
            uiModels
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Error reading error body: ${ex.message}"
            }
            Log.e(TAG, "getRecommendedRecipes HTTP error: ${e.code()} - $errorBody")
            Log.e(TAG, "HTTP exception details", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getRecommendedRecipes unexpected exception: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipesByCategory(userId: String, category: String, token: String): List<RecipeDetailUiModel> {
        Log.d(TAG, "getRecipesByCategory called with userId: $userId, category: $category")
        return try {
            Log.d(TAG, "Making API call to get recipes by category with filter: eq.$category")
            val result = service.getRecipesByCategory(
                token = "Bearer $token",
                category = "eq.$category"
            )
            Log.d(TAG, "getRecipesByCategory API success: ${result.size} recipes received for category: $category")

            Log.d(TAG, "==RECIPES FOR CATEGORY: $category==")
            result.forEach { recipe ->
                Log.d(TAG, "Recipe ${recipe.id}: $recipe")
            }

            Log.d(TAG, "Converting ${result.size} category recipes to UI models")
            val uiModels = result.map { it.toPreviewUi(userId) }
            Log.d(TAG, "Successfully converted to ${uiModels.size} category UI models")
            uiModels
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Error reading error body: ${ex.message}"
            }
            Log.e(TAG, "getRecipesByCategory HTTP error for category '$category': ${e.code()} - $errorBody")
            Log.e(TAG, "HTTP exception details", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getRecipesByCategory unexpected exception for category '$category': ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun likeRecipe(recipeId: String, userId: String, token: String, recipeOwner: String): LikeResponse? {
        Log.d(TAG, "likeRecipe called with recipeId: $recipeId, userId: $userId")
        return try {
            Log.d(TAG, "Making API call to like/unlike recipe")
            val requestBody = mapOf("recipe_id" to recipeId, "user_id" to userId, "recipe_owner" to recipeOwner)
            Log.d(TAG, "Request body: $requestBody")

            val response = service.likeRecipe(
                token = "Bearer $token",
                body = requestBody
            )

            Log.d(TAG, "Like recipe API response - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            if (response.isSuccessful) {
                val likeResponse = response.body()
                Log.d(TAG, "Like recipe successful: $likeResponse")
                likeResponse
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (ex: Exception) {
                    "Error reading error body: ${ex.message}"
                }
                Log.e(TAG, "Like recipe failed with code ${response.code()}: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "likeRecipe exception for recipeId: $recipeId, userId: $userId - ${e.message}", e)
            null
        }
    }

    override suspend fun saveRecipe(recipeId: String, userId: String, token: String, recipeOwner: String): SaveResponse? {
        Log.d(TAG, "saveRecipe called with recipeId: $recipeId, userId: $userId")
        return try {
            Log.d(TAG, "Making API call to save/unsave recipe")
            val requestBody = mapOf("recipe_id" to recipeId, "user_id" to userId, "recipe_owner" to recipeOwner)
            Log.d(TAG, "Request body: $requestBody")

            val response = service.saveRecipe(
                token = "Bearer $token",
                body = requestBody
            )

            Log.d(TAG, "Save recipe API response - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            if (response.isSuccessful) {
                val saveResponse = response.body()
                Log.d(TAG, "Save recipe successful: $saveResponse")
                saveResponse
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (ex: Exception) {
                    "Error reading error body: ${ex.message}"
                }
                Log.e(TAG, "Save recipe failed with code ${response.code()}: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveRecipe exception for recipeId: $recipeId, userId: $userId - ${e.message}", e)
            null
        }
    }

    override suspend fun getComments(recipeId: String, token: String): List<CommentUiModel> {
        Log.d(TAG, "getComments called with recipeId: $recipeId")
        return try {
            Log.d(TAG, "Making API call to get comments with filter: eq.$recipeId")
            val response = service.getCommentsForRecipe(
                token = "Bearer $token",
                recipeId = "eq.$recipeId"
            )

            Log.d(TAG, "Comments API success: Server returned ${response.size} comments")
            response.forEachIndexed { index, comment ->
                Log.d(TAG, "Comment $index: id=${comment.id}, userId=${comment.userId}, parentId=${comment.parentCommentId}, content='${comment.content}'")
            }

            Log.d(TAG, "Building nested comment structure")
            val nestedComments = buildNestedComments(response)
            Log.d(TAG, "Successfully built ${nestedComments.size} root comments with nested replies")
            nestedComments
        } catch (e: Exception) {
            Log.e(TAG, "getComments exception for recipeId: $recipeId - ${e.message}", e)
            emptyList()
        }
    }

    private fun buildNestedComments(flatList: List<CommentResponse>): List<CommentUiModel> {
        Log.d(TAG, "buildNestedComments called with ${flatList.size} flat comments")

        if (flatList.isEmpty()) {
            Log.d(TAG, "No comments to build - returning empty list")
            return emptyList()
        }

        // Step 1: Convert all to UI models
        Log.d(TAG, "Step 1: Converting all comments to UI models")
        val allComments = mutableMapOf<String, CommentUiModel>()

        flatList.forEach { comment ->
            val displayName = comment.userProfile?.displayName ?: "Unknown"
            val avatarUrl = comment.userProfile?.avatarUrl

            val uiComment = CommentUiModel(
                id = comment.id,
                recipeId = comment.recipeId,
                userId = comment.userId,
                displayName = displayName,
                avatarUrl = avatarUrl,
                content = comment.content,
                createdAt = comment.createdAt,
                parentId = comment.parentCommentId,
                children = emptyList()
            )
            allComments[comment.id] = uiComment
            Log.d(TAG, "Converted comment ${comment.id} to UI model - displayName: $displayName, hasAvatar: ${avatarUrl != null}")
        }

        // Step 2: Find root comments and direct replies only
        Log.d(TAG, "Step 2: Organizing comments into root and reply structure")
        val rootComments = mutableListOf<CommentUiModel>()
        val repliesMap = mutableMapOf<String, MutableList<CommentUiModel>>()

        flatList.forEach { comment ->
            val uiComment = allComments[comment.id]!!

            if (comment.parentCommentId == null) {
                // This is a root comment
                Log.d(TAG, "Found root comment: ${comment.id}")
                rootComments.add(uiComment)
            } else {
                // This is a reply - find the root parent
                Log.d(TAG, "Processing reply comment: ${comment.id}, parentId: ${comment.parentCommentId}")
                val rootParentId = findRootParent(flatList, comment.id)
                if (rootParentId != null) {
                    Log.d(TAG, "Reply ${comment.id} belongs to root comment: $rootParentId")
                    repliesMap.getOrPut(rootParentId) { mutableListOf() }.add(uiComment)
                } else {
                    Log.w(TAG, "Could not find root parent for reply: ${comment.id}")
                }
            }
        }

        // Step 3: Attach all replies to root comments (flattened)
        Log.d(TAG, "Step 3: Attaching replies to root comments")
        val result = rootComments.map { rootComment ->
            val allReplies = repliesMap[rootComment.id] ?: emptyList()
            Log.d(TAG, "Root comment ${rootComment.id} has ${allReplies.size} replies")

            val sortedReplies = allReplies.sortedBy { it.createdAt }
            Log.d(TAG, "Sorted ${allReplies.size} replies by creation time for root comment ${rootComment.id}")

            rootComment.copy(children = sortedReplies)
        }.sortedBy { it.createdAt }

        Log.d(TAG, "Built flattened comment tree: ${result.size} root comments")
        result.forEach { rootComment ->
            Log.d(TAG, "Final: Root comment ${rootComment.id} has ${rootComment.children.size} total replies")
            rootComment.children.forEach { reply ->
                Log.d(TAG, "  Reply: ${reply.id} by ${reply.displayName}")
            }
        }

        return result
    }

    // Helper to find the root parent of any comment
    private fun findRootParent(flatList: List<CommentResponse>, commentId: String): String? {
        Log.d(TAG, "findRootParent called for commentId: $commentId")

        val comment = flatList.find { it.id == commentId }
        if (comment == null) {
            Log.w(TAG, "Comment not found: $commentId")
            return null
        }

        return if (comment.parentCommentId == null) {
            // This is already a root comment
            Log.d(TAG, "Comment $commentId is already a root comment")
            comment.id
        } else {
            // Find the root of the parent
            Log.d(TAG, "Comment $commentId has parent ${comment.parentCommentId}, finding root recursively")
            findRootParent(flatList, comment.parentCommentId)
        }
    }

    override suspend fun addCommentAndUpdateCount(
        token: String,
        recipeId: String,
        userId: String,
        content: String,
        parentCommentId: String?,
        recipeOwner: String
    ): AddCommentResponse? {
        Log.d(TAG, "addCommentAndUpdateCount called")
        Log.d(TAG, "Parameters - recipeId: $recipeId, userId: $userId, parentCommentId: $parentCommentId, recipeOwner: $recipeOwner")
        Log.d(TAG, "Content length: ${content.length}, preview: '${content.take(50)}${if (content.length > 50) "..." else ""}'")

        return try {
            val requestBody = mapOf(
                "recipe_id" to recipeId,
                "user_id" to userId,
                "content" to content,
                "parent_comment_id" to parentCommentId,
                "recipe_owner" to recipeOwner
            )
            Log.d(TAG, "Request body prepared: $requestBody")

            Log.d(TAG, "Making API call to add comment and update count")
            val response = service.addCommentAndUpdateCount(
                token = "Bearer $token",
                body = requestBody
            )

            Log.d(TAG, "Add comment API response - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            if (response.isSuccessful) {
                val addCommentResponse = response.body()
                Log.d(TAG, "Add comment successful: $addCommentResponse")
                addCommentResponse
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (ex: Exception) {
                    "Error reading error body: ${ex.message}"
                }
                Log.e(TAG, "Add comment failed with code ${response.code()}: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "addCommentAndUpdateCount exception - recipeId: $recipeId, userId: $userId - ${e.message}", e)
            null
        }
    }
}
