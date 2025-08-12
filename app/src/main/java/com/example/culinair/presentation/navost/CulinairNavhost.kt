package com.example.culinair.presentation.navost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culinair.presentation.DeepLinkResult
import com.example.culinair.presentation.screens.auth.LoginScreen
import com.example.culinair.presentation.screens.auth.RegisterScreen
import com.example.culinair.presentation.screens.main.MainScreen

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@Composable
fun CulinairNavHost(
    navController: NavHostController = rememberNavController(),
    deepLinkResult: DeepLinkResult? = null
) {

    LaunchedEffect(deepLinkResult) {
        deepLinkResult?.let {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { RegisterScreen(navController) }
        composable("main") {
            MainScreen(deepLinked = deepLinkResult != null, culinairNavController = navController)
        }
    }
}