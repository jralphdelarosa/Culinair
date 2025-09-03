package com.example.culinair.data.repository

import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.data.remote.apiservice.SupabaseAuthService
import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.model.AuthMethod
import com.example.culinair.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.http.conn.ConnectTimeoutException
import java.security.AuthProvider
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
sealed class RegisterResult {
    data class EmailConfirmationSent(val message: String) : RegisterResult()
    data class Success(val session: UserSession) : RegisterResult()
    data class Error(val error: Throwable) : RegisterResult()
}

class AuthRepositoryImpl @Inject constructor(
    private val service: SupabaseAuthService,
    private val sessionManager: SessionManager,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    private suspend fun refreshSession(): Boolean {
        val refreshToken = sessionManager.getRefreshToken() ?: return false

        return try {
            val response = service.refreshToken(mapOf("refresh_token" to refreshToken))
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse?.access_token != null && authResponse.refresh_token != null) {
                    sessionManager.saveUserSession(
                        authResponse.access_token,
                        authResponse.refresh_token,
                        authResponse.user?.id ?: "",
                        sessionManager.getAuthMethod() ?: AuthMethod.EmailPassword
                    )
                    true
                } else false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    //method to check and restore session on app start
    override suspend fun restoreSession(): Result<UserSession?> {
        return try {
            if (!sessionManager.isLoggedIn()) {
                return Result.success(null)
            }

            val accessToken = sessionManager.getAccessToken()
            val refreshToken = sessionManager.getRefreshToken()
            val userId = sessionManager.getUserId()
            val authMethod = sessionManager.getAuthMethod()

            if (accessToken != null && refreshToken != null && userId != null && authMethod != null) {
                // Verify the token is still valid by making a test request
                val isValid = verifyTokenValidity()

                if (isValid) {
                    val session = UserSession(accessToken, refreshToken, authMethod, userId)
                    Result.success(session)
                } else {
                    // Try to refresh the token
                    val refreshResult = refreshSession()
                    if (refreshResult) {
                        // Get the new tokens after refresh
                        val newAccessToken = sessionManager.getAccessToken()
                        val newRefreshToken = sessionManager.getRefreshToken()
                        if (newAccessToken != null && newRefreshToken != null) {
                            val session =
                                UserSession(newAccessToken, newRefreshToken, authMethod, userId)
                            Result.success(session)
                        } else {
                            // Clear invalid session
                            sessionManager.clearSession()
                            Result.success(null)
                        }
                    } else {
                        // Refresh failed, clear session
                        sessionManager.clearSession()
                        Result.success(null)
                    }
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Session restoration failed", e)
            // Clear potentially corrupted session
            sessionManager.clearSession()
            Result.success(null)
        }
    }

    //helper method to verify token validity
    private suspend fun verifyTokenValidity(): Boolean {
        return try {
            // Make a simple request to verify the token is still valid
            val response = service.checkProfile(
                mapOf("limit" to "1")
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signUp(email: String, password: String): Result<RegisterResult> {
        return try {
            Log.d("AuthRepository", "=== SIGN UP DEBUG ===")
            Log.d("AuthRepository", "Email: $email")
            Log.d("AuthRepository", "Password length: ${password.length}")

            val requestBody = mapOf("email" to email, "password" to password)
            Log.d("AuthRepository", "Request body: $requestBody")

            val response = service.signUp(requestBody)
            val raw = response.body()?.toString()

            Log.d("AuthRepository", "=== SIGN UP RESPONSE ===")
            Log.d("AuthRepository", "Status: ${response.code()}")
            Log.d("AuthRepository", "Headers:")
            response.headers().forEach { header ->
                Log.d("AuthRepository", "  ${header.first}: ${header.second}")
            }
            Log.d("AuthRepository", "Response body: $raw")

            if (response.isSuccessful) {
                Log.d("AuthRepository", "✅ Sign up successful - confirmation email sent")
                Result.success(
                    RegisterResult.EmailConfirmationSent(
                        "A confirmation email has been sent to $email. Please verify to complete registration."
                    )
                )
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "AuthRepository",
                    "❌ Sign up failed - Status: ${response.code()}, Error: $errorBody"
                )
                Result.failure(Exception("Signup failed: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Sign up exception", e)
            Result.success(RegisterResult.Error(e))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserSession> {
        return try {
            val requestBody = mapOf("email" to email, "password" to password)
            val response = service.signIn(requestBody)

            if (response.isSuccessful) {
                val authResponse = response.body()
                Log.d("AuthRepository", "Auth response: $authResponse")

                if (authResponse?.access_token != null &&
                    authResponse.refresh_token != null &&
                    authResponse.user?.id != null
                ) {

                    sessionManager.saveUserSession(
                        authResponse.access_token,
                        authResponse.refresh_token,
                        authResponse.user.id,
                        AuthMethod.EmailPassword
                    )
                    Result.success(
                        UserSession(
                            authResponse.access_token,
                            authResponse.refresh_token,
                            AuthMethod.EmailPassword,
                            authResponse.user.id
                        )
                    )
                } else {
                    Result.failure(Exception("Missing token or user ID"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserSession> {
        return try {
            Log.d("AuthRepository", "=== GOOGLE SIGN IN DEBUG ===")
            Log.d("AuthRepository", "ID Token present: ${idToken.isNotBlank()}")
            Log.d("AuthRepository", "ID Token preview: ${idToken.take(50)}...")

            val requestBody = mapOf(
                "provider" to "google",
                "id_token" to idToken
            )

            val response = service.signInWithGoogle(requestBody)
            Log.d("AuthRepository", "Response code: ${response.code()}")
            Log.d("AuthRepository", "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val authResponse = response.body()
                Log.d("AuthRepository", "Google auth response: $authResponse")

                if (authResponse?.access_token != null &&
                    authResponse.refresh_token != null &&
                    authResponse.user?.id != null
                ) {

                    val userSession = UserSession(
                        authResponse.access_token,
                        authResponse.refresh_token,
                        AuthMethod.Google,
                        authResponse.user.id
                    )

                    sessionManager.saveUserSession(
                        authResponse.access_token,
                        authResponse.refresh_token,
                        authResponse.user.id,
                        AuthMethod.Google
                    )

                    // Ensure profile exists for Google users too
                    ensureProfileExists(authResponse.user.id, authResponse.access_token)

                    Result.success(userSession)
                } else {
                    Result.failure(Exception("Missing token or user ID from Google auth"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "AuthRepository",
                    "Google sign in failed: ${response.code()}, Error: $errorBody"
                )
                Log.e("AuthRepository", "Response message: ${response.message()}")
                Result.failure(Exception("Google login failed: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google sign in exception", e)
            Result.failure(e)
        }
    }


    override suspend fun signOut(): Result<Unit> {
        return try {
            Log.d("AuthRepository", "=== UNIVERSAL SIGN OUT START ===")

            val accessToken = sessionManager.getAccessToken()
            val authMethod = sessionManager.getAuthMethod()

            if (accessToken == null) {
                Log.w("AuthRepository", "No access token found for Supabase sign out")

                // Still sign out from Google if it was a Google session
                if (authMethod is AuthMethod.Google) {
                    googleSignInClient.signOut().await()
                    Log.d("AuthRepository", "Google sign out completed (no Supabase token)")
                }

                sessionManager.clearSession()
                return Result.success(Unit)
            }

            // 1. Sign out from Supabase (works for both email and Google auth)
            val response = service.signOut("Bearer $accessToken")

            if (response.isSuccessful) {
                Log.d("AuthRepository", "Supabase sign out successful")

                // 2. Sign out from Google if user was authenticated via Google
                if (authMethod is AuthMethod.Google) {
                    googleSignInClient.signOut().await()
                    Log.d("AuthRepository", "Google sign out successful")
                }

                // 3. Clear local session data
                sessionManager.clearSession()
                Log.d("AuthRepository", "Local session cleared")

                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "AuthRepository",
                    "Supabase sign out failed: ${response.code()}, Error: $errorBody"
                )

                // Still attempt Google sign out and clear local data even if server fails
                if (authMethod is AuthMethod.Google) {
                    googleSignInClient.signOut().await()
                    Log.d("AuthRepository", "Google sign out completed despite server error")
                }

                sessionManager.clearSession()
                Result.failure(Exception("Server sign out failed: $errorBody"))
            }

        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign out exception", e)

            // Attempt to clean up local state even if there's an error
            try {
                val authMethod = sessionManager.getAuthMethod()
                if (authMethod is AuthMethod.Google) {
                    googleSignInClient.signOut().await()
                    Log.d("AuthRepository", "Google cleanup successful")
                }
                sessionManager.clearSession()
            } catch (cleanupException: Exception) {
                Log.e("AuthRepository", "Cleanup also failed", cleanupException)
            }

            Result.failure(e)
        }
    }


    override suspend fun ensureProfileExists(userId: String, token: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            Log.d("AuthRepository", "=== ENSURE PROFILE EXISTS DEBUG ===")
            Log.d("AuthRepository", "User ID: $userId")
            Log.d("AuthRepository", "Token present: ${token.isNotBlank()}")

            val maxRetries = 3
            val retryDelayMillis = 2000L

            repeat(maxRetries) { attempt ->
                try {
                    Log.d("AuthRepository", "=== PROFILE CHECK ATTEMPT ${attempt + 1} ===")

                    // Interceptor will automatically add authorization header
                    val checkResponse = service.checkProfile(mapOf("id" to "eq.$userId"))

                    Log.d("AuthRepository", "Check profile response:")
                    Log.d("AuthRepository", "Status: ${checkResponse.code()}")

                    if (checkResponse.isSuccessful) {
                        val body = checkResponse.body()
                        Log.d("AuthRepository", "Profile check body: $body")

                        if (body.isNullOrEmpty()) {
                            Log.d("AuthRepository", "=== CREATING NEW PROFILE ===")

                            val profile = JsonObject().apply {
                                addProperty("id", userId)
                            }

                            // Interceptor will automatically add authorization header
                            val insertResponse = service.createProfile(profile)

                            if (!insertResponse.isSuccessful) {
                                val errorBody = insertResponse.errorBody()?.string()
                                Log.e(
                                    "AuthRepository",
                                    "❌ Profile insert failed - Status: ${insertResponse.code()}, Error: $errorBody"
                                )
                                return@withContext Result.failure(Exception("Insert failed: ${insertResponse.code()} - $errorBody"))
                            } else {
                                Log.d("AuthRepository", "✅ Profile created successfully")
                            }
                        } else {
                            Log.d("AuthRepository", "✅ Profile already exists")
                        }

                        return@withContext Result.success(Unit)
                    } else {
                        val errorBody = checkResponse.errorBody()?.string()
                        Log.e(
                            "AuthRepository",
                            "❌ Profile check failed - Status: ${checkResponse.code()}, Error: $errorBody"
                        )
                        return@withContext Result.failure(Exception("Check failed: ${checkResponse.code()} - $errorBody"))
                    }
                } catch (e: ConnectTimeoutException) {
                    Log.w("AuthRepository", "⏰ Timeout on attempt ${attempt + 1}: ${e.message}")
                    if (attempt < maxRetries - 1) {
                        delay(retryDelayMillis)
                    } else {
                        return@withContext Result.failure(e)
                    }
                } catch (e: Exception) {
                    Log.e(
                        "AuthRepository",
                        "❌ Profile ensure exception on attempt ${attempt + 1}",
                        e
                    )
                    return@withContext Result.failure(e)
                }
            }

            return@withContext Result.failure(Exception("Retries exhausted"))
        }
}