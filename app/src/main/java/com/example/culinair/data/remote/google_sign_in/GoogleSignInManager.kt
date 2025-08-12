package com.example.culinair.data.remote.google_sign_in

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.culinair.domain.model.GoogleSignInResult
import com.example.culinair.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
@Singleton
class GoogleSignInManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val webClientId = Constants.WEB_CLIENT_ID // From Supabase config

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent {
        Log.d("GoogleSignInManager", "Creating Google Sign-In intent")
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?): GoogleSignInResult {
        Log.d("GoogleSignInManager", "=== HANDLING GOOGLE SIGN-IN RESULT ===")
        Log.d("GoogleSignInManager", "Data is null: ${data == null}")

        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("GoogleSignInManager", "Task created successfully")

            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignInManager", "Account: ${account?.email}")
            Log.d("GoogleSignInManager", "Display Name: ${account?.displayName}")
            Log.d("GoogleSignInManager", "ID: ${account?.id}")

            val idToken = account?.idToken
            Log.d("GoogleSignInManager", "ID Token present: ${idToken != null}")
            Log.d("GoogleSignInManager", "ID Token length: ${idToken?.length}")

            if (idToken != null) {
                Log.d("GoogleSignInManager", "✅ Google Sign-In successful")
                GoogleSignInResult.Success(idToken)
            } else {
                Log.e("GoogleSignInManager", "❌ ID Token is null - Web Client ID might be wrong")
                GoogleSignInResult.Error("Failed to get ID token - check Web Client ID configuration")
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignInManager", "❌ ApiException: ${e.statusCode} - ${e.message}")
            Log.e("GoogleSignInManager", "Status code details:")
            when (e.statusCode) {
                CommonStatusCodes.SIGN_IN_REQUIRED -> Log.e("GoogleSignInManager", "SIGN_IN_REQUIRED")
                CommonStatusCodes.INVALID_ACCOUNT -> Log.e("GoogleSignInManager", "INVALID_ACCOUNT")
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> Log.e("GoogleSignInManager", "SIGN_IN_CANCELLED")
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> Log.e("GoogleSignInManager", "SIGN_IN_FAILED")
                GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> Log.e("GoogleSignInManager", "SIGN_IN_CURRENTLY_IN_PROGRESS")
                GoogleSignInStatusCodes.NETWORK_ERROR -> Log.e("GoogleSignInManager", "NETWORK_ERROR")
                else -> Log.e("GoogleSignInManager", "Unknown status code: ${e.statusCode}")
            }

            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> GoogleSignInResult.Cancelled
                else -> GoogleSignInResult.Error("Google Sign-In failed: ${e.statusCode} - ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("GoogleSignInManager", "❌ Unexpected exception", e)
            GoogleSignInResult.Error("Unexpected error: ${e.message}")
        }
    }
}