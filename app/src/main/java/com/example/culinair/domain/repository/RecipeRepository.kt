package com.example.culinair.domain.repository

import android.net.Uri
import com.example.culinair.data.remote.model.request.CreateRecipeRequest
import com.example.culinair.data.remote.model.response.Recipe

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
interface RecipeRepository {
    suspend fun createRecipe(recipe: CreateRecipeRequest): Result<Unit>
    suspend fun uploadRecipeImage(imageUri: Uri): Result<String>
    suspend fun getRecipes(): Result<List<Recipe>>
}