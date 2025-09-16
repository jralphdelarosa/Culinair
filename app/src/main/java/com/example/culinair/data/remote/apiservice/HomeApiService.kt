package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.model.response.AddCommentResponse
import com.example.culinair.data.remote.model.response.CommentResponse
import com.example.culinair.data.remote.model.response.RecipeResponse
import com.example.culinair.data.remote.model.response.LikeResponse
import com.example.culinair.data.remote.model.response.SaveResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */

interface HomeApiService {

    @GET("rest/v1/recipes")
    suspend fun getAllPublicRecipes(
        @Header("Authorization") token: String,
        @Query("select") select: String = SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES,
        @Query("is_public") isPublic: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 20
    ): List<RecipeResponse>

    @GET("rest/v1/recipes")
    suspend fun getFollowingRecipes(
        @Header("Authorization") token: String,
        @Query("select") select: String = SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES,
        @Query("user_id") userId: String, // passed as user_id=eq.<uuid>
        @Query("is_public") isPublic: String = "eq.true", // must be formatted this way
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 20
    ): List<RecipeResponse>

    @GET("rest/v1/recipes")
    suspend fun getTrendingRecipes(
        @Header("Authorization") token: String,
        @Query("select") select: String = SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES,
        @Query("is_public") isPublic: String = "eq.true",
        @Query("order") order: String = "likes_count.desc,saves_count.desc",
        @Query("limit") limit: Int = 10
    ): List<RecipeResponse>

    @GET("rest/v1/recipes")
    suspend fun getRecommendedRecipes(
        @Header("Authorization") token: String,
        @Query("select") select: String = SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES,
        @Query("is_public") isPublic: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 15
    ): List<RecipeResponse>

    @GET("rest/v1/recipes")
    suspend fun getRecipesByCategory(
        @Header("Authorization") token: String,
        @Query("select") select: String = SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES,
        @Query("category") category: String,
        @Query("is_public") isPublic: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 20
    ): List<RecipeResponse>

    @POST("rest/v1/rpc/like_recipe")
    suspend fun likeRecipe(
        @Header("Authorization") token: String,
        @Body body: Map<String, String> // { "recipe_id": "abc-123", "user_id": "def-456" }
    ): Response<LikeResponse>

    @POST("rest/v1/rpc/save_recipe")
    suspend fun saveRecipe(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<SaveResponse>

    @GET("rest/v1/comments")
    suspend fun getCommentsForRecipe(
        @Header("Authorization") token: String,
        @Query("recipe_id") recipeId: String, // pass "eq.<recipeId>"
        @Query("select") select: String = SELECT_COMMENT_WITH_USER,
        @Query("order") order: String = "created_at.asc"
    ): List<CommentResponse>

    @POST("rest/v1/rpc/add_comment_and_update_count")
    suspend fun addCommentAndUpdateCount(
        @Header("Authorization") token: String,
        @Body body: Map<String, String?> // recipe_id, user_id, content, parent_comment_id
    ): Response<AddCommentResponse>

    companion object {
        const val SELECT_COMMENT_WITH_USER  = "*, user_profiles!left(id, display_name, avatar_url)"
        // This query will check if current user liked the recipe
        const val SELECT_RECIPE_WITH_USER_AND_LIKES_SAVES  = "*, user_profiles!left(id, display_name, avatar_url), recipe_likes!left(user_id), recipe_saves!left(user_id)"
    }
}
