package com.example.culinair.presentation.viewmodel.auth

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.repository.RegisterResult
import com.example.culinair.data.remote.model.response.UserSession
import com.example.culinair.data.remote.google_sign_in.GoogleSignInManager
import com.example.culinair.domain.model.GoogleSignInResult
import com.example.culinair.domain.model.SessionRestorationState
import com.example.culinair.domain.usecase.auth.GoogleSignInUseCase
import com.example.culinair.domain.usecase.auth.LoginUseCase
import com.example.culinair.domain.usecase.auth.RegisterUseCase
import com.example.culinair.domain.usecase.auth.RestoreSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val restoreSessionUseCase: RestoreSessionUseCase,
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    var loginState by mutableStateOf<Result<UserSession>?>(null)
        private set

    var registerState by mutableStateOf<Result<RegisterResult>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isRegistering by mutableStateOf(false)
        private set

    private val _session = mutableStateOf<UserSession?>(null)
    val session: State<UserSession?> = _session

    var googleSignInState by mutableStateOf<Result<UserSession>?>(null)
        private set

    // Add session restoration state
    var sessionRestorationState by mutableStateOf<SessionRestorationState>(SessionRestorationState.Loading)
        private set

    // Call this method when the app starts
    fun restoreSession() {
        viewModelScope.launch {
            sessionRestorationState = SessionRestorationState.Loading

            val result = restoreSessionUseCase()
            result.onSuccess { userSession ->
                if (userSession != null) {
                    _session.value = userSession
                    sessionRestorationState = SessionRestorationState.Success(userSession)
                } else {
                    sessionRestorationState = SessionRestorationState.NoSession
                }
            }.onFailure { exception ->
                sessionRestorationState = SessionRestorationState.Error(exception)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginState = loginUseCase(email, password)
            isLoading = false
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            isRegistering = true
            registerState = registerUseCase(email, password)
            isRegistering = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading = true
            googleSignInState = googleSignInUseCase(idToken)
            isLoading = false
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInManager.getSignInIntent()
    }

    fun handleGoogleSignInResult(data: Intent?) {
        when (val result = googleSignInManager.handleSignInResult(data)) {
            is GoogleSignInResult.Success -> signInWithGoogle(result.idToken)
            is GoogleSignInResult.Error -> {
                googleSignInState = Result.failure(Exception(result.message))
            }
            GoogleSignInResult.Cancelled -> {
                // Handle cancellation if needed
            }
        }
    }

    fun saveSession(session: UserSession) {
        _session.value = session
        // Save to DataStore here too if you want to persist it across app restarts
    }

    fun clearState() {
        loginState = null
        registerState = null
    }
}