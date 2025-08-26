package com.example.culinair.domain.usecase.comment

import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/26/2025.
 */
class GetCommentsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(recipeId: String, token: String): List<CommentUiModel> {
        return repository.getComments(recipeId, token)
    }
}