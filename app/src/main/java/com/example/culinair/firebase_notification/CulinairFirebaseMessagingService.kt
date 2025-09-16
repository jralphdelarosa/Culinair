package com.example.culinair.firebase_notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.culinair.R
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.repository.NotificationsRepository
import com.example.culinair.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 9/9/2025.
 */
@AndroidEntryPoint
class CulinairFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "culinair_notifications"
        private const val CHANNEL_NAME = "Culinair Notifications"
        private const val NOTIFICATION_ID = 1
    }

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    // Create a coroutine scope for async operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // Send token to your server
        // Let the manager handle it - removes duplicate logic
        serviceScope.launch {
            try {
                fcmTokenManager.handleTokenRefresh(token)  // Add this method to manager
                Log.d(TAG, "FCM token handled by manager")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to handle FCM token refresh", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            showNotification(it.title, it.body, remoteMessage.data)
        }

        // Refresh notifications in the app
        serviceScope.launch {
            try {
                // This will update the notification list and unread count
                // You might want to use a shared event bus or repository method
                Log.d(TAG, "Triggering notification refresh")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to refresh notifications", e)
            }
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"]
        val notificationId = data["notification_id"]
        val recipeId = data["recipe_id"]
        val actorName = data["actor_name"]

        Log.d(TAG, "Handling data message - type: $type, notificationId: $notificationId")

        when (type) {
            "LIKE" -> {
                showNotification(
                    title = "Recipe Liked!",
                    body = "$actorName liked your recipe",
                    data = data
                )
            }
            "COMMENT" -> {
                showNotification(
                    title = "New Comment",
                    body = "$actorName commented on your recipe",
                    data = data
                )
            }
            "FOLLOW" -> {
                showNotification(
                    title = "New Follower",
                    body = "$actorName is now following you",
                    data = data
                )
            }
            "SAVE" -> {
                showNotification(
                    title = "Recipe Saved!",
                    body = "$actorName saved your recipe",
                    data = data
                )
            }
        }
    }

    private fun showNotification(title: String?, body: String?, data: Map<String, String>) {
        // Create intent for when notification is tapped
        val intent = createNotificationIntent(data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.culinair_logo) // Add your notification icon
            .setContentTitle(title ?: "Culinair")
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationIntent(data: Map<String, String>): Intent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Add extras based on notification type
        val type = data["type"]
        val recipeId = data["recipe_id"]
        val notificationId = data["notification_id"]

        when (type) {
            "LIKE", "COMMENT", "SAVE" -> {
                // Navigate to recipe detail
                recipeId?.let {
                    intent.putExtra("navigate_to", "recipe_detail")
                    intent.putExtra("recipe_id", it)
                }
            }
            "FOLLOW" -> {
                // Navigate to profile or notifications
                intent.putExtra("navigate_to", "notifications")
            }
            else -> {
                intent.putExtra("navigate_to", "notifications")
            }
        }

        notificationId?.let {
            intent.putExtra("notification_id", it)
        }

        return intent
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for likes, comments, follows, and saves"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun saveTokenLocally(token: String) {
        val prefs = getSharedPreferences("culinair_fcm", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "FCM token saved locally in service")
    }
}
