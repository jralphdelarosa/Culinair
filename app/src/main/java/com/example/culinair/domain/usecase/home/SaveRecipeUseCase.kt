package com.example.culinair.domain.usecase.home

import com.example.culinair.data.remote.dto.response.SaveResponse
import com.example.culinair.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/7/2025.
 */
class SaveRecipeUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(recipeId: String, userId: String, token: String, recipeOwner: String): SaveResponse? {
        return repository.saveRecipe(recipeId, userId, token, recipeOwner)
    }
}