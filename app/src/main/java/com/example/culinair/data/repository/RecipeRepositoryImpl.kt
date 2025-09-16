package com.example.culinair.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.RecipeApiService
import com.example.culinair.data.remote.model.request.CreateRecipeRequest
import com.example.culinair.data.remote.model.response.Recipe
import com.example.culinair.domain.repository.RecipeRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val apiService: RecipeApiService,
    private val context: Context,
    private val sessionManager: SessionManager
) : RecipeRepository {

    companion object {
        private const val TAG = "RecipeRepositoryImpl"
    }

    override suspend fun createRecipe(recipe: CreateRecipeRequest): Result<Unit> {
        Log.d(TAG, "createRecipe: Starting recipe creation")
        Log.d(TAG, "createRecipe: Recipe title = ${recipe.title}")

        val token = sessionManager.getAccessToken() ?: return Result.failure(Exception("No access token"))

        Log.d(TAG, "createRecipe: Token retrieved = $token")

        // Log the complete request payload
        Log.d(TAG, "createRecipe: Complete request payload:")
        Log.d(TAG, "  - title: ${recipe.title}")
        Log.d(TAG, "  - description: ${recipe.description}")
        Log.d(TAG, "  - ingredients: ${recipe.ingredients}")
        Log.d(TAG, "  - steps: ${recipe.steps}")
        Log.d(TAG, "  - imageUrl: ${recipe.imageUrl}")
        Log.d(TAG, "  - category: ${recipe.category}")
        Log.d(TAG, "  - tags: ${recipe.tags}")
        Log.d(TAG, "  - cookTimeMinutes: ${recipe.cookTimeMinutes}")
        Log.d(TAG, "  - difficulty: ${recipe.difficulty}")
        Log.d(TAG, "  - userId: ${recipe.userId}")

        return try {
            Log.d(TAG, "createRecipe: Making API call to create recipe")
            val response = apiService.createRecipe(
                token = "Bearer $token",
                recipe = recipe
            )

            Log.d(
                TAG,
                "createRecipe: API response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}"
            )

            if (response.isSuccessful) {
                Log.d(TAG, "createRecipe: Recipe created successfully.")
                Result.success(Unit)
            } else {
                val errorMsg = "Failed to create recipe: ${response.message()}"
                Log.e(TAG, "createRecipe: $errorMsg")
                Log.e(TAG, "createRecipe: Response code: ${response.code()}")
                response.errorBody()?.let { errorBody ->
                    Log.e(TAG, "createRecipe: Error body: ${errorBody.string()}")
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "createRecipe: Exception occurred", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadRecipeImage(imageUri: Uri): Result<String> {
        Log.d(TAG, "uploadRecipeImage: Starting image upload")
        Log.d(TAG, "uploadRecipeImage: Image URI = $imageUri")

        val token = sessionManager.getAccessToken() ?: return Result.failure(Exception("No access token"))
        val recipeId = UUID.randomUUID().toString()
        Log.d(TAG, "uploadRecipeImage: Token retrieved, length = ${token.length}")

        return try {
            val fileName = "recipe.jpg"
            Log.d(TAG, "uploadRecipeImage: Generated filename = $fileName")

            Log.d(TAG, "uploadRecipeImage: Opening input stream from URI")
            val inputStream = context.contentResolver.openInputStream(imageUri)

            val requestFile = inputStream?.let { stream ->
                Log.d(TAG, "uploadRecipeImage: Reading bytes from input stream")
                val bytes = stream.readBytes()
                Log.d(TAG, "uploadRecipeImage: Image size = ${bytes.size} bytes")
                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            }

            if (requestFile != null) {
                Log.d(TAG, "uploadRecipeImage: Creating multipart body")
                val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

                Log.d(TAG, "uploadRecipeImage: Making API call to upload image")
                val response = apiService.uploadImage(
                    recipeId = recipeId,
                    fileName = fileName,
                    token = token,
                    image = body
                )

                Log.d(
                    TAG,
                    "uploadRecipeImage: API response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}"
                )

                if (response.isSuccessful) {
                    val imageUrl =
                        "https://voygyldtkkbwdljnfwfg.supabase.co/storage/v1/object/public/recipe-images/$recipeId/$fileName"
                    Log.d(TAG, "uploadRecipeImage: Image uploaded successfully")
                    Log.d(TAG, "uploadRecipeImage: Image URL = $imageUrl")
                    Result.success(imageUrl)
                } else {
                    val errorMsg = "Failed to upload image"
                    Log.e(TAG, "uploadRecipeImage: $errorMsg")
                    Log.e(TAG, "uploadRecipeImage: Response code: ${response.code()}")
                    Log.e(TAG, "uploadRecipeImage: Response message: ${response.message()}")
                    response.errorBody()?.let { errorBody ->
                        Log.e(TAG, "uploadRecipeImage: Error body: ${errorBody.string()}")
                    }
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMsg = "Failed to read image"
                Log.e(TAG, "uploadRecipeImage: $errorMsg - requestFile is null")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "uploadRecipeImage: Exception occurred", e)
            Result.failure(e)
        }
    }

    override suspend fun getRecipes(): Result<List<Recipe>> {
        Log.d(TAG, "getRecipes: Starting to fetch recipes")

        val userId = sessionManager.getUserId() ?: return Result.failure(Exception("Not authenticated"))
        val token = sessionManager.getAccessToken() ?: return Result.failure(Exception("No access token"))

        Log.d(TAG, "getRecipes: Token retrieved, length = ${token?.length ?: 0}")

        return try {
            Log.d(TAG, "getRecipes: Making API call to fetch recipes")
            val response = apiService.getRecipes(
                token = token,
                userId =  "eq.$userId"
            )

            Log.d(
                TAG,
                "getRecipes: API response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}"
            )

            if (response.isSuccessful && response.body() != null) {
                val recipes = response.body()!!
                Log.d(TAG, "getRecipes: Recipes fetched successfully, count = ${recipes.size}")
                recipes.forEachIndexed { index, recipe ->
                    Log.d(
                        TAG,
                        "getRecipes: Recipe $index - ID: ${recipe.id}, Title: ${recipe.title}"
                    )
                }
                Result.success(recipes)
            } else {
                val errorMsg = "Failed to fetch recipes"
                Log.e(TAG, "getRecipes: $errorMsg")
                Log.e(TAG, "getRecipes: Response code: ${response.code()}")
                Log.e(TAG, "getRecipes: Response message: ${response.message()}")
                response.errorBody()?.let { errorBody ->
                    Log.e(TAG, "getRecipes: Error body: ${errorBody.string()}")
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecipes: Exception occurred", e)
            Result.failure(e)
        }
    }
}
