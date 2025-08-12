package com.example.culinair.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.culinair.data.remote.dto.response.UserSession
import com.example.culinair.domain.repository.AuthRepository
import com.example.culinair.presentation.navost.CulinairNavHost
import com.example.culinair.presentation.theme.DishlyTheme
import com.example.culinair.presentation.viewmodel.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLinkData = extractDeepLink(intent)
        // Call ensureProfileExists if this is a confirmation deep link
        if (deepLinkData != null && deepLinkData.fromConfirmation) {
            lifecycleScope.launch {
                // ✅ Save session first
                val session = UserSession(
                    accessToken = deepLinkData.accessToken,
                    refreshToken = deepLinkData.refreshToken,
                    userId = deepLinkData.userId
                )
                val viewModel: AuthViewModel = ViewModelProvider(this@MainActivity)[AuthViewModel::class.java]
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
            DishlyTheme {
                CulinairNavHost(deepLinkResult = deepLinkData)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent reference
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
}

data class DeepLinkResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val fromConfirmation: Boolean = true // default true since only the deeplink sets it
)