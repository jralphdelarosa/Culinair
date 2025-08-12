package com.example.culinair.domain.model

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
    object Cancelled : GoogleSignInResult()
}