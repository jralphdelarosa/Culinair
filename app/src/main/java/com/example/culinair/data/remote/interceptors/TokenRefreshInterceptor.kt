package com.example.culinair.data.remote.interceptors

import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.SupabaseAuthService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/29/2025.
 */
@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    @Named("token_refresh_service") private val tokenRefreshService: SupabaseAuthService, // Use the separate service
    private val sessionExpiryHandler: SessionExpiryHandler
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            response.close()

            val refreshed = runBlocking { refreshTokenSync() }

            if (refreshed) {
                val newAccessToken = runBlocking { sessionManager.getAccessToken() }
                if (newAccessToken != null) {
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                }
            }

            // Notify that session has expired
            sessionExpiryHandler.notifySessionExpired()

            return Response.Builder()
                .request(originalRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized - Session expired")
                .body(ResponseBody.create(null, ""))
                .build()
        }

        return response
    }

    private suspend fun refreshTokenSync(): Boolean {
        return try {
            val refreshToken = sessionManager.getRefreshToken() ?: return false
            val authMethod = sessionManager.getAuthMethod() ?: return false

            // Use the dedicated token refresh service (no interceptors)
            val response = tokenRefreshService.refreshToken(mapOf("refresh_token" to refreshToken))

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse?.access_token != null && authResponse.refresh_token != null) {
                    val userId = sessionManager.getUserId() ?: authResponse.user?.id ?: ""
                    sessionManager.saveUserSession(
                        authResponse.access_token,
                        authResponse.refresh_token,
                        userId,
                        authMethod
                    )
                    Log.d("TokenRefreshInterceptor", "✅ Token refreshed successfully")
                    true
                } else false
            } else {
                Log.e("TokenRefreshInterceptor", "❌ Token refresh failed: ${response.code()}")
                sessionManager.clearSession()
                false
            }
        } catch (e: Exception) {
            Log.e("TokenRefreshInterceptor", "❌ Token refresh exception", e)
            sessionManager.clearSession()
            false
        }
    }
}

@Singleton
class SessionExpiryHandler @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 0)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun notifySessionExpired() {
        GlobalScope.launch {
            sessionManager.clearSession()
            _sessionExpired.emit(Unit)
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionExpiryEntryPoint {
    fun sessionExpiryHandler(): SessionExpiryHandler
}