package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.model.request.FCMTokenRequest
import com.example.culinair.data.remote.model.response.FCMTokenResponse
import com.example.culinair.data.remote.model.response.NotificationResponse
import com.example.culinair.domain.model.CountResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by John Ralph Dela Rosa on 9/2/2025.
 */
interface NotificationApiService {

    // List notifications for the signed-in user (RLS handles user_id = auth.uid())
    // NOTE: If you want actor profile info embedded, you can later switch `select` to an embedded join.
    @GET("rest/v1/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("select") select: String = "id,user_id,actor_id,type,recipe_id,message,is_read,created_at",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 50
    ): List<NotificationResponse>

    // Single delete (used by swipe-to-delete)
    @DELETE("rest/v1/notifications")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Query("id") idEq: String // pass as "eq.<uuid>"
    ): Response<ResponseBody>

    // Unread count (for the badge)
    @GET("rest/v1/notifications")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String,
        @Query("select") select: String = "count",
        @Query("is_read") isRead: String = "eq.false"
    ): List<CountResponse>

    @PATCH("rest/v1/notifications")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Query("id") idEq: String, // pass as "eq.<uuid>"
        @Body body: Map<String, Boolean> = mapOf("is_read" to true)
    ): Response<ResponseBody>

    // FCM Token management
    @POST("rest/v1/fcm_tokens")
    suspend fun saveFcmToken(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String,
        @Body tokenData: FCMTokenRequest
    ): Response<ResponseBody>

    @PATCH("rest/v1/fcm_tokens")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Query("user_id") userIdEq: String,
        @Query("device_id") deviceIdEq: String,
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    @DELETE("rest/v1/fcm_tokens")
    suspend fun deleteFcmToken(
        @Header("Authorization") token: String,
        @Query("token") tokenEq: String // pass as "eq.token_value"
    ): Response<ResponseBody>

    @GET("rest/v1/fcm_tokens")
    suspend fun getUserFcmTokens(
        @Header("Authorization") token: String,
        @Query("select") select: String = "id,token,device_id,created_at"
    ): List<FCMTokenResponse>
}