package com.example.culinair.data.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/27/2025.
 */
data class AddCommentResponse(
    @SerializedName("comment_id")
    val commentId: String,
    @SerializedName("comments_count")
    val commentsCount: Int
)