package com.example.culinair.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinair.presentation.dialogs.WelcomeDialog
import com.example.culinair.presentation.navost.BottomNavScreen.Companion.bottomNavItems
import com.example.culinair.presentation.navost.MainNavHost
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGold
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.notifications.NotificationsViewModel

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun MainScreen(
    deepLinked: Boolean,
    culinairNavController: NavController,
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val mainTabsNavController = rememberNavController()
    var showWelcomeDialog by remember { mutableStateOf(deepLinked) }

    val currentBackStack by mainTabsNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val hideBottomBar = currentRoute?.let { route ->
        route.startsWith("recipe_detail") ||
                route.startsWith("other_profile") ||
                route.startsWith("settings")
    } ?: false

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // Standard bottom nav height
                        .background(BrandBackground)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bottomNavItems.forEach { screen ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    mainTabsNavController.navigate(screen.route) {
                                        popUpTo("home") { inclusive = false }
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(28.dp) // Give the box a fixed size for better positioning
                            ) {
                                Icon(
                                    screen.icon,
                                    contentDescription = screen.label,
                                    tint = if (currentRoute == screen.route) BrandGold else BrandGreen,
                                    modifier = Modifier.size(24.dp)
                                )

                                if (screen.route == "notifications" && notificationsViewModel.unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(Color.Red, shape = CircleShape)
                                            .align(Alignment.TopEnd)
                                            .offset(y = (-4).dp), // Fine-tune positioning
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (notificationsViewModel.unreadCount > 99) "99+" else notificationsViewModel.unreadCount.toString(),
                                            color = Color.White,
                                            fontSize = 8.sp, // Slightly smaller font
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = screen.label,
                                fontSize = 10.sp,
                                color = if (currentRoute == screen.route) BrandGold else BrandGreen,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val contentPadding = if (hideBottomBar) PaddingValues(0.dp) else innerPadding

        Box(modifier = Modifier.padding(contentPadding)) {
            MainNavHost(
                navController = mainTabsNavController,
                onLogout = {
                    culinairNavController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                notificationsViewModel = notificationsViewModel
            )
        }

        if (showWelcomeDialog) {
            WelcomeDialog(onDismiss = { showWelcomeDialog = false })
        }
    }
}