package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.dto.request.UpdateProfileRequest
import com.example.culinair.data.remote.dto.response.ApiResponse
import com.example.culinair.data.remote.dto.response.FollowToggleResponse
import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.domain.model.CountResponse
import com.example.culinair.domain.model.FollowRequest
import com.example.culinair.domain.model.FollowerResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
interface ProfileApiService {

    @GET("rest/v1/user_profiles")
    suspend fun getProfile(
        @Query("id") id: String,  // This will receive "eq.{userId}"
        @Header("Authorization") auth: String
    ): Response<List<ProfileResponse>>

    @PATCH("rest/v1/user_profiles")  // Changed from POST to PATCH
    suspend fun updateProfile(
        @Query("id") id: String,  // This should use "eq.{value}" format
        @Header("Authorization") auth: String,
        @Body updateRequest: UpdateProfileRequest
    ): Response<ApiResponse>

    @POST("storage/v1/object/avatars/{userId}/{fileName}")
    suspend fun uploadAvatarRaw(
        @Path("userId") userId: String,
        @Path("fileName") fileName: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "image/jpeg",
        @Header("x-upsert") upsert: String = "true",
        @Body image: RequestBody
    ): Response<ResponseBody>

    @POST("storage/v1/object/cover-photo/{userId}/{fileName}")
    suspend fun uploadCoverPhotoRaw(
        @Path("userId") userId: String,
        @Path("fileName") fileName: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "image/jpeg",
        @Header("x-upsert") upsert: String = "true",
        @Body image: RequestBody
    ): Response<ResponseBody>

    // Follower endpoints
    @GET("rest/v1/followers")
    suspend fun getFollowerCount(
        @Query("following_id") followingId: String,
        @Query("select") select: String = "count",
        @Header("Authorization") auth: String
    ): Response<List<CountResponse>>

    @GET("rest/v1/followers")
    suspend fun getFollowingCount(
        @Query("follower_id") followerId: String,
        @Query("select") select: String = "count",
        @Header("Authorization") auth: String
    ): Response<List<CountResponse>>

    @GET("rest/v1/followers")
    suspend fun checkIfFollowing(
        @Query("follower_id") followerId: String,
        @Query("following_id") followingId: String,
        @Header("Authorization") auth: String
    ): Response<List<FollowerResponse>>

//    @POST("rest/v1/followers")
//    suspend fun followUser(
//        @Header("Authorization") auth: String,
//        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
//        @Body followRequest: FollowRequest
//    ): Response<ResponseBody>
//
//    @DELETE("rest/v1/followers")
//    suspend fun unfollowUser(
//        @Query("follower_id") followerId: String,
//        @Query("following_id") followingId: String,
//        @Header("Authorization") auth: String
//    ): Response<ResponseBody>

    @POST("rest/v1/rpc/follow_user")
    suspend fun followUserRpc(
        @Header("Authorization") token: String,
        @Body body: Map<String, String> // { "follower_id": "...", "following_id": "..." }
    ): Response<FollowToggleResponse>

}