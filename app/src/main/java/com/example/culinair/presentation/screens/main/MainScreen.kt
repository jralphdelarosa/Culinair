package com.example.culinair.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinair.presentation.dialogs.WelcomeDialog
import com.example.culinair.presentation.navost.BottomNavScreen.Companion.bottomNavItems
import com.example.culinair.presentation.navost.MainNavHost
import com.example.culinair.presentation.theme.BrandBackgroundYellow
import com.example.culinair.presentation.theme.BrandGold

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun MainScreen(deepLinked: Boolean, culinairNavController: NavController) {
    val mainTabsNavController = rememberNavController()
    var showWelcomeDialog by remember { mutableStateOf(deepLinked) }

    val currentBackStack by mainTabsNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = BrandBackgroundYellow,
                modifier = Modifier.navigationBarsPadding() // Add bottom padding
            ) {
                bottomNavItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = currentRoute == screen.route,
                        onClick = {
                            mainTabsNavController.navigate(screen.route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        selectedContentColor = BrandGold,
                        unselectedContentColor = Color(0xFF2F4F4F).copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavHost(
                navController = mainTabsNavController,
                onLogout = {
                    culinairNavController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                })
        }

        if (showWelcomeDialog) {
            WelcomeDialog(onDismiss = { showWelcomeDialog = false })
        }
    }
}