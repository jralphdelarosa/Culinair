package com.example.culinair.presentation.navost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
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
import com.example.culinair.presentation.screens.community.CommunityScreen
import com.example.culinair.presentation.screens.recipe_detail.RecipeDetailScreen
import com.example.culinair.presentation.viewmodel.recipe.RecipeViewModel

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun MainNavHost(
    navController: NavHostController,
    onLogout: () -> Unit,
    recipeViewModel: RecipeViewModel = hiltViewModel()
) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(recipeViewModel = recipeViewModel, navController = navController) }
        composable("discover") { DiscoverScreen() }
        composable("post") { PostDishScreen() }
        composable("community") { CommunityScreen() }
        composable("profile") {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(recipeViewModel = recipeViewModel, recipeId = recipeId, navController = navController)
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
    object Community : BottomNavScreen("community", "Connect", Icons.Default.Groups)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(Home, Discover, Post, Community, Profile)
    }
}