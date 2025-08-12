package com.example.culinair.domain.usecase.home

import com.example.culinair.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
class GetRecipesByCategoryUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(userId: String, category: String, token: String) =
        repository.getRecipesByCategory(userId, category,token)
}