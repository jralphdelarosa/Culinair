package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.model.request.CreateRecipeRequest
import com.example.culinair.data.remote.model.response.Recipe
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
interface RecipeApiService {
    @POST("rest/v1/recipes")
    suspend fun createRecipe(
        @Header("Authorization") token: String?,
        @Body recipe: CreateRecipeRequest
    ): Response<Unit>

    @GET("rest/v1/recipes")
    suspend fun getRecipes(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String? = null,
        @Query("select") select: String = "*"
    ): Response<List<Recipe>>

    @Multipart
    @POST("storage/v1/object/recipe-images/{recipeId}/{fileName}")
    suspend fun uploadImage(
        @Path("recipeId") recipeId: String?,
        @Path("fileName") fileName: String,
        @Header("Authorization") token: String?,
        @Header("x-upsert") upsert: String = "true",
        @Part image: MultipartBody.Part
    ): Response<Unit>
}