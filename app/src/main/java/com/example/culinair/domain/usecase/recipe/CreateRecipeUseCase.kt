package com.example.culinair.domain.usecase.recipe

import android.net.Uri
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.dto.request.CreateRecipeRequest
import com.example.culinair.data.remote.dto.response.Recipe
import com.example.culinair.domain.repository.RecipeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
class CreateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        ingredients: List<String>,
        steps: List<String>,
        imageUri: Uri?,
        category: String,
        tags: List<String>,
        cookTimeMinutes: Int,
        difficulty: String,
    ): Result<Unit> {

        // Validation
        if (title.isBlank()) return Result.failure(Exception("Title is required"))
        if (ingredients.isEmpty()) return Result.failure(Exception("At least one ingredient is required"))
        if (steps.isEmpty()) return Result.failure(Exception("At least one step is required"))

        return try {
            // Upload image first if provided
            val imageUrl = if (imageUri != null) {
                repository.uploadRecipeImage(imageUri).getOrElse { exception ->
                    return Result.failure(exception)
                }
            } else ""

            // Create recipe
            val createRequest = CreateRecipeRequest(
                title = title,
                description = description,
                ingredients = ingredients.filter { it.isNotBlank() },
                steps = steps.filter { it.isNotBlank() },
                imageUrl = imageUrl,
                category = category,
                tags = tags.filter { it.isNotBlank() },
                cookTimeMinutes = cookTimeMinutes,
                difficulty = difficulty,
                userId = sessionManager.getUserId().toString()
            )

            repository.createRecipe(createRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}