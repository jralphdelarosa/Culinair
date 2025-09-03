package com.example.culinair.domain.model

/**
 * Created by John Ralph Dela Rosa on 8/12/2025.
 */
//sealed class for auth methods
sealed class AuthMethod {
    object EmailPassword : AuthMethod()
    object Google : AuthMethod()
}