package com.example.culinair.presentation.navost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.culinair.presentation.screens.SettingsScreen
import com.example.culinair.presentation.screens.auth.LoginScreen
import com.example.culinair.presentation.screens.discover.DiscoverScreen
import com.example.culinair.presentation.screens.home.HomeScreen
import com.example.culinair.presentation.screens.post_dish.PostDishScreen
import com.example.culinair.presentation.screens.profile.ProfileScreen
import com.example.culinair.presentation.screens.saved.SavedScreen

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun MainNavHost(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("discover") { DiscoverScreen() }
        composable("post") { PostDishScreen() }
        composable("saved") { SavedScreen() }
        composable("profile") {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onLogoutClick = onLogout
            )
        }
    }
}

sealed class BottomNavScreen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen("home", "Home", Icons.Default.Home)
    object Discover : BottomNavScreen("discover", "Discover", Icons.Default.Search)
    object Post : BottomNavScreen("post", "Post", Icons.Default.AddCircle)
    object Saved : BottomNavScreen("saved", "Saved", Icons.Default.Favorite)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(Home, Discover, Post, Saved, Profile)
    }
}