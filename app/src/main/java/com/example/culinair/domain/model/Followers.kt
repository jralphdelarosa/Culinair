package com.example.culinair.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Created by John Ralph Dela Rosa on 8/30/2025.
 */
data class FollowerResponse(
    @SerializedName("follower_id")
    val followerId: String,
    @SerializedName("following_id")
    val followingId: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class FollowRequest(
    @SerializedName("follower_id")
    val followerId: String,
    @SerializedName("following_id")
    val followingId: String
)

data class CountResponse(
    val count: Int
)

data class UserStats(
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false
)