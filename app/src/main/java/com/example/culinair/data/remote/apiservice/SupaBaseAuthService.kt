package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.dto.request.UpdateProfileRequest
import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.data.remote.dto.response.SupabaseAuthResponse
import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.model.SupabaseGoogleAuthResponse
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
interface SupabaseAuthService {

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<SupabaseAuthResponse>

    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: Map<String, String>): Response<ResponseBody>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body request: Map<String, String>): Response<SupabaseAuthResponse>

    // Add Google Sign-In endpoint
    @POST("auth/v1/token?grant_type=id_token")
    suspend fun signInWithGoogle(@Body request: Map<String, String>): Response<SupabaseGoogleAuthResponse>

    // Sign out endpoint - works for both email and Google auth
    @POST("auth/v1/logout")
    suspend fun signOut(@Header("Authorization") authHeader: String): Response<ResponseBody>

    @GET("rest/v1/user_profiles")
    suspend fun checkProfile(
        @QueryMap filters: Map<String, String>,
        @Header("Authorization") authHeader: String
    ): Response<List<ProfileResponse>>

    @POST("rest/v1/user_profiles")
    suspend fun createProfile(
        @Header("Authorization") authorization: String,
        @Body profile: JsonObject
    ): Response<Unit>
}