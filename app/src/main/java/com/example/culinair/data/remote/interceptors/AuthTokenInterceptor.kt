package com.example.culinair.data.remote.interceptors

import com.example.culinair.data.local.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/29/2025.
 */
@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token to auth endpoints to avoid conflicts
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("/auth/v1/")

        if (isAuthEndpoint) {
            return chain.proceed(originalRequest)
        }

        // Get token and add to request
        val accessToken = runBlocking { sessionManager.getAccessToken() }

        val requestWithToken = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestWithToken)
    }
}