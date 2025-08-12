package com.example.culinair.domain.usecase.home

import com.example.culinair.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/7/2025.
 */
class GetAllPublicRecipesUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(userId: String, token: String) =
        repository.getAllPublicRecipes(userId, token)
}