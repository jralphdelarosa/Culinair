package com.example.culinair.presentation.navost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.culinair.presentation.screens.settings.SettingsScreen
import com.example.culinair.presentation.screens.discover.DiscoverScreen
import com.example.culinair.presentation.screens.home.HomeScreen
import com.example.culinair.presentation.screens.post_dish.PostDishScreen
import com.example.culinair.presentation.screens.profile.ProfileScreen
import com.example.culinair.presentation.screens.notifications.NotificationsScreen
import com.example.culinair.presentation.screens.other_profile.OtherProfileScreen
import com.example.culinair.presentation.screens.recipe_detail.RecipeDetailScreen
import com.example.culinair.presentation.viewmodel.notifications.NotificationsViewModel
import com.example.culinair.presentation.viewmodel.recipe.RecipeViewModel

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun MainNavHost(
    navController: NavHostController,
    onLogout: () -> Unit,
    recipeViewModel: RecipeViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel
) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                recipeViewModel = recipeViewModel,
                navController = navController
            )
        }
        composable("discover") { DiscoverScreen() }
        composable("post") { PostDishScreen() }
        composable("notifications") {
            NotificationsScreen(
                viewModel = notificationsViewModel,
                navHostController = navController,
                onRecipeNotificationClick = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                recipeViewModel = recipeViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                }
            )
        }
        composable(
            route = "other_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            OtherProfileScreen(
                userId = userId,
                recipeViewModel = recipeViewModel, // Pass the recipeViewModel here
                onBack = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                }
            )
        }
        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeViewModel = recipeViewModel,
                recipeId = recipeId,
                navController = navController
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
    object Notifications :
        BottomNavScreen("notifications", "Notifications", Icons.Default.Notifications)

    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(Home, Discover, Post, Notifications, Profile)
    }
}