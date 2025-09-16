package com.example.culinair.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.repository.AuthRepository
import com.example.culinair.firebase_notification.FCMTokenManager
import com.example.culinair.presentation.navost.CulinairNavHost
import com.example.culinair.presentation.theme.CulinairTheme
import com.example.culinair.presentation.viewmodel.auth.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: AuthViewModel = ViewModelProvider(this@MainActivity)[AuthViewModel::class.java]
        val deepLinkData = extractDeepLink(intent)

        // Request notification permission BEFORE initializing FCM
        requestNotificationPermission()

        // Initialize FCM
        fcmTokenManager.initializeFCM()

        // Handle notification intent (when app is opened from notification)
        handleNotificationIntent(intent)

        // Call ensureProfileExists if this is a confirmation deep link
        if (deepLinkData != null && deepLinkData.fromConfirmation) {
            lifecycleScope.launch {
                // ✅ Save session first
                val session = UserSession(
                    accessToken = deepLinkData.accessToken,
                    refreshToken = deepLinkData.refreshToken,
                    userId = deepLinkData.userId
                )

                viewModel.saveSession(session)

                // ✅ Then ensure profile
                val result = authRepository.ensureProfileExists(
                    userId = session.userId ?: "",
                    token = session.accessToken ?: ""
                )

                result.onFailure {
                    Log.e("ProfileSaveDebug", "❌ Failed to ensure profile: ${it.message}")
                }

                result.onSuccess {
                    Log.d("ProfileSaveDebug", "✅ Profile ensured for user: ${session.userId}")
                }
            }
        }


        setContent {
            CulinairTheme {
                CulinairNavHost(authViewModel = viewModel, deepLinkResult = deepLinkData)
            }
        }

        debugFCMToken()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Notification permission already granted")
                }
                else -> {
                    Log.d("MainActivity", "Requesting notification permission")
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {
            Log.d("MainActivity", "Notification permission not needed for this Android version")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
        setIntent(intent) // Update the intent reference
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Notification permission granted")
                    // Reinitialize FCM if needed
                    fcmTokenManager.initializeFCM()
                } else {
                    Log.w("MainActivity", "Notification permission denied")
                    // Handle permission denial - maybe show a dialog explaining why notifications are needed
                }
            }
        }
    }

    private fun debugFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MainActivity", "Current FCM Token: $token")
                // Copy this token to test with Firebase Console
            } else {
                Log.e("MainActivity", "Failed to get FCM token", task.exception)
            }
        }
    }

    private fun extractDeepLink(intent: Intent?): DeepLinkResult? {
        val uri = intent?.data ?: return null
        val fragment = uri.fragment ?: return null
        val params = fragment.split("&").associate {
            val (k, v) = it.split("=")
            k to v
        }

        val accessToken = params["access_token"]
        val refreshToken = params["refresh_token"]
        val userId = params["user_id"] ?: decodeSubFromToken(accessToken)

        return if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && !userId.isNullOrBlank()) {
            DeepLinkResult(accessToken, refreshToken, userId)
        } else null
    }

    private fun decodeSubFromToken(token: String?): String? {
        return try {
            val payload = token?.split(".")?.getOrNull(1)
            val decoded = payload?.let { String(Base64.decode(it, Base64.URL_SAFE)) }
            val json = JSONObject(decoded ?: return null)
            json.getString("sub")
        } catch (e: Exception) {
            null
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val navigateTo = it.getStringExtra("navigate_to")
            val recipeId = it.getStringExtra("recipe_id")
            val notificationId = it.getStringExtra("notification_id")

            Log.d("MainActivity", "Handling notification intent: $navigateTo")

            when (navigateTo) {
                "recipe_detail" -> {
                    recipeId?.let { id ->
                        Log.d("MainActivity", "Navigate to recipe: $id")
                        // Add your navigation logic here
                        // For example: navController.navigate("recipe_detail/$id")
                    }
                }
                "notifications" -> {
                    Log.d("MainActivity", "Navigate to notifications")
                    // Add your navigation logic here
                    // For example: navController.navigate("notifications")
                }
            }

            // Mark notification as read if notification_id is present
            notificationId?.let { id ->
                Log.d("MainActivity", "Mark notification as read: $id")
                // You can call your ViewModel method here
            }
        }
    }
}

data class DeepLinkResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val fromConfirmation: Boolean = true // default true since only the deeplink sets it
)