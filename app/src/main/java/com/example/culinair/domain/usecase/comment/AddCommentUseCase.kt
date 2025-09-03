package com.example.culinair.domain.usecase.comment

import com.example.culinair.data.remote.dto.response.AddCommentResponse
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/26/2025.
 */
class AddCommentUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    /**
     * Inserts a comment via RPC and returns the new comments_count (and new comment_id).
     */
    suspend operator fun invoke(
        token: String,
        userId: String,
        recipeId: String,
        content: String,
        parentCommentId: String? = null,
        recipeOwner: String
    ): AddCommentResponse? {
        return repository.addCommentAndUpdateCount(
            token = token,
            recipeId = recipeId,
            userId = userId,
            content = content,
            parentCommentId = parentCommentId,
            recipeOwner = recipeOwner
        )
    }
}