package com.example.culinair.firebase_notification

import android.content.Context
import android.util.Log
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.usecase.notifications.DeleteFcmTokenUseCase
import com.example.culinair.domain.usecase.notifications.SaveFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
@Singleton
class FCMTokenManager @Inject constructor(
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase,
    private val deleteFcmTokenUseCase: DeleteFcmTokenUseCase,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "FCMTokenManager"
        private const val TOKEN_WAIT_TIMEOUT = 10000L
        private const val PREFS_NAME = "culinair_fcm"
        private const val KEY_LAST_TOKEN = "last_fcm_token"
    }

    private var pendingToken: String? = null
    private val sharedPrefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun initializeFCM() {
        Log.d(TAG, "Initializing FCM...")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "FCM Token received: ${token.take(20)}...")

            // Check if this is a new token
            val lastToken = sharedPrefs.getString(KEY_LAST_TOKEN, null)
            if (lastToken != token) {
                Log.d(TAG, "New FCM token detected")
                saveLastToken(token)

                CoroutineScope(Dispatchers.IO).launch {
                    handleNewToken(token)
                }
            } else {
                Log.d(TAG, "FCM token unchanged")
                // Still try to sync if user is logged in but token might not be on server
                CoroutineScope(Dispatchers.IO).launch {
                    if (sessionManager.isLoggedIn()) {
                        saveTokenToServer(token)

                    }
                }
            }
        }
    }

    private fun saveLastToken(token: String) {
        sharedPrefs.edit { putString(KEY_LAST_TOKEN, token) }
    }

    private suspend fun handleNewToken(token: String) {
        try {
            if (sessionManager.isLoggedIn()) {
                Log.d(TAG, "User is logged in, saving token to server")
                saveTokenToServer(token)
            } else {
                Log.d(TAG, "User not logged in, storing token for later")
                pendingToken = token
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling FCM token", e)
        }
    }

    fun handleUserLogin() {
        Log.d(TAG, "User logged in, handling FCM token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Wait for session to be fully established
                waitForValidSession()

                // Use pending token if available, otherwise get current token
                val token = pendingToken ?: getCurrentFCMToken()

                if (token != null) {
                    saveTokenToServer(token)
                    pendingToken = null // Clear pending token
                    saveLastToken(token)
                } else {
                    Log.e(TAG, "Could not get FCM token after login")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during user login token handling", e)
            }
        }
    }

    fun handleUserLogout() {
        Log.d(TAG, "User logging out, removing FCM token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = pendingToken ?: getCurrentFCMToken()
                if (token != null) {
                    val result = deleteFcmTokenUseCase(token)
                    result.fold(
                        onSuccess = { Log.d(TAG, "FCM token removed from server") },
                        onFailure = { Log.e(TAG, "Failed to remove FCM token from server", it) }
                    )
                }

                // Clear local data
                pendingToken = null
                sharedPrefs.edit { remove(KEY_LAST_TOKEN) }

            } catch (e: Exception) {
                Log.e(TAG, "Error during user logout token handling", e)
            }
        }
    }

    private suspend fun waitForValidSession() {
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < TOKEN_WAIT_TIMEOUT) {
            val accessToken = sessionManager.getAccessToken()
            if (accessToken != null) {
                Log.d(TAG, "Valid session found, proceeding with token save")
                return
            }

            Log.d(TAG, "Waiting for valid session... (${System.currentTimeMillis() - startTime}ms)")
            delay(500)
        }

        Log.e(TAG, "Timeout waiting for valid session")
        throw Exception("Session not available after timeout")
    }

    private suspend fun getCurrentFCMToken(): String? {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    Log.e(TAG, "Failed to get FCM token", task.exception)
                    continuation.resume(null)
                }
            }
        }
    }

    suspend fun handleTokenRefresh(token: String) {
        Log.d(TAG, "Handling token refresh: ${token.take(20)}...")

        // Check if this is actually a new token
        val lastToken = sharedPrefs.getString(KEY_LAST_TOKEN, null)
        if (lastToken != token) {
            Log.d(TAG, "Token refresh with new token")
            saveLastToken(token)
            pendingToken = token
            handleNewToken(token)
        } else {
            Log.d(TAG, "Token refresh but token unchanged")
        }
    }

    private suspend fun saveTokenToServer(token: String) {
        try {
            Log.d(TAG, "Attempting to save FCM token to server")

            val accessToken = sessionManager.getAccessToken()
            if (accessToken == null) {
                Log.e(TAG, "No access token available, cannot save FCM token")
                return
            }

            val result = saveFcmTokenUseCase(token)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "FCM token successfully saved to server")
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to save FCM token to server", exception)
                    // Don't clear the token, keep it for retry
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception saving FCM token", e)
        }
    }

    // Method to force token refresh/resync
    suspend fun forceSyncToken() {
        try {
            val token = getCurrentFCMToken()
            if (token != null && sessionManager.isLoggedIn()) {
                saveTokenToServer(token)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error force syncing token", e)
        }
    }
}