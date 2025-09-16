package com.example.culinair.data.remote.apiservice

import com.example.culinair.data.remote.model.response.ProfileResponse
import com.example.culinair.data.remote.model.response.SupabaseAuthResponse
import com.example.culinair.domain.model.SupabaseGoogleAuthResponse
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @POST("auth/v1/token?grant_type=id_token")
    suspend fun signInWithGoogle(@Body request: Map<String, String>): Response<SupabaseGoogleAuthResponse>

    // Keep the auth header for sign out since it's an auth endpoint
    @POST("auth/v1/logout")
    suspend fun signOut(@Header("Authorization") authHeader: String): Response<ResponseBody>

    // Remove @Header annotations - interceptor will handle them
    @GET("rest/v1/user_profiles")
    suspend fun checkProfile(
        @QueryMap filters: Map<String, String>
    ): Response<List<ProfileResponse>>

    @POST("rest/v1/user_profiles")
    suspend fun createProfile(
        @Body profile: JsonObject
    ): Response<Unit>
}