package com.example.culinair.presentation.navost

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culinair.data.remote.interceptors.SessionExpiryEntryPoint
import com.example.culinair.domain.model.SessionRestorationState
import com.example.culinair.presentation.DeepLinkResult
import com.example.culinair.presentation.components.CircularLogoWithLoadingRing
import com.example.culinair.presentation.screens.auth.LoginScreen
import com.example.culinair.presentation.screens.auth.RegisterScreen
import com.example.culinair.presentation.screens.main.MainScreen
import com.example.culinair.presentation.viewmodel.auth.AuthViewModel
import dagger.hilt.android.EntryPointAccessors

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@Composable
fun CulinairNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    deepLinkResult: DeepLinkResult? = null
) {
    val context = LocalContext.current
    val sessionExpiryHandler = EntryPointAccessors.fromApplication(
        context.applicationContext,
        SessionExpiryEntryPoint::class.java
    ).sessionExpiryHandler()

    LaunchedEffect(Unit) {
        sessionExpiryHandler.sessionExpired.collect {
            // Session expired, navigate back to login
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (deepLinkResult == null) { // Only restore if not handling deep link
            authViewModel.restoreSession()
        }
    }

    LaunchedEffect(deepLinkResult) {
        deepLinkResult?.let {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authViewModel.sessionRestorationState) {
        when (val state = authViewModel.sessionRestorationState) {
            is SessionRestorationState.Success -> {
                if (deepLinkResult == null) {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is SessionRestorationState.NoSession -> {
                // Stay on login screen
            }
            is SessionRestorationState.Error -> {
                Log.e("Navigation", "Session restoration failed", state.exception)
            }
            SessionRestorationState.Loading -> {
                // Show loading
            }
        }
    }


    when (authViewModel.sessionRestorationState) {
        SessionRestorationState.Loading -> {
            // Show a loading screen while checking session
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLogoWithLoadingRing()
            }
        }
        else -> {
            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("signup") { RegisterScreen(navController) }
                composable("main") {
                    MainScreen(
                        deepLinked = deepLinkResult != null,
                        culinairNavController = navController
                    )
                }
            }
        }
    }
}