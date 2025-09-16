package com.example.culinair.di

import android.content.Context
import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.HomeApiService
import com.example.culinair.data.remote.apiservice.NotificationApiService
import com.example.culinair.data.remote.apiservice.ProfileApiService
import com.example.culinair.data.remote.apiservice.RecipeApiService
import com.example.culinair.data.remote.apiservice.SupabaseAuthService
import com.example.culinair.data.remote.interceptors.AuthTokenInterceptor
import com.example.culinair.data.remote.interceptors.TokenRefreshInterceptor
import com.example.culinair.data.repository.AuthRepositoryImpl
import com.example.culinair.data.repository.HomeRepositoryImpl
import com.example.culinair.data.repository.NotificationsRepositoryImpl
import com.example.culinair.data.repository.ProfileRepositoryImpl
import com.example.culinair.data.repository.RecipeRepositoryImpl
import com.example.culinair.domain.repository.AuthRepository
import com.example.culinair.domain.repository.HomeRepository
import com.example.culinair.domain.repository.NotificationsRepository
import com.example.culinair.domain.repository.ProfileRepository
import com.example.culinair.domain.repository.RecipeRepository
import com.example.culinair.firebase_notification.FCMTokenManager
import com.example.culinair.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
* Created by John Ralph Dela Rosa on 8/4/2025.
*/
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("supabase_url")
    fun provideSupabaseUrl(): String = Constants.SUPABASE_URL

    @Provides
    @Singleton
    @Named("supabase_anon_key")
    fun provideSupabaseAnonKey(): String = Constants.SUPABASE_ANON_KEY

    // Basic OkHttpClient without interceptors for token refresh
    @Provides
    @Singleton
    @Named("token_refresh_client")
    fun provideTokenRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Separate Retrofit instance for token refresh (no interceptors)
    @Provides
    @Singleton
    @Named("token_refresh_retrofit")
    fun provideTokenRefreshRetrofit(
        @Named("token_refresh_client") okHttpClient: OkHttpClient,
        @Named("supabase_url") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Service for token refresh only
    @Provides
    @Singleton
    @Named("token_refresh_service")
    fun provideTokenRefreshService(
        @Named("token_refresh_retrofit") retrofit: Retrofit
    ): SupabaseAuthService {
        return retrofit.create(SupabaseAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("supabase_anon_key") anonKey: String,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        authTokenInterceptor: AuthTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authTokenInterceptor) // Add token to requests
            .addInterceptor(tokenRefreshInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("apikey", anonKey)
                val request = requestBuilder.build()
                // Log all request details
                Log.d("NetworkModule", "=== HTTP REQUEST ===")
                Log.d("NetworkModule", "Method: ${request.method}")
                Log.d("NetworkModule", "URL: ${request.url}")
                Log.d("NetworkModule", "Headers:")
                request.headers.forEach { header ->
                    Log.d("NetworkModule", "  ${header.first}: ${header.second}")
                }

                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @Named("supabase_url") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthService(retrofit: Retrofit): SupabaseAuthService {
        return retrofit.create(SupabaseAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationsApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        service: SupabaseAuthService,
        sessionManager: SessionManager,
        googleSignInClient: GoogleSignInClient,
        fcmTokenManager: FCMTokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(service, sessionManager, googleSignInClient, fcmTokenManager)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        sessionManager: SessionManager,
        service: ProfileApiService
    ): ProfileRepository {
        return ProfileRepositoryImpl(context,sessionManager,service)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        @ApplicationContext context: Context,
        service: RecipeApiService,
        sessionManager: SessionManager,
    ): RecipeRepository {
        return RecipeRepositoryImpl(service, context, sessionManager)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        service: HomeApiService,
    ): HomeRepository {
        return HomeRepositoryImpl(service)
    }

    @Provides
    @Singleton
    fun provideNotificationsRepository(
        service: NotificationApiService,
        sessionManager: SessionManager
    ): NotificationsRepository {
        return NotificationsRepositoryImpl(sessionManager, service)
    }
}