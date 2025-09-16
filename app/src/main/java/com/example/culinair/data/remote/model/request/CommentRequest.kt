package com.example.culinair.data.remote.model.request

/**
 * Created by John Ralph Dela Rosa on 8/26/2025.
 */
data class CommentRequest(
    val recipe_id: String,
    val content: String,
    val parent_comment_id: String? = null // null for normal comment
)