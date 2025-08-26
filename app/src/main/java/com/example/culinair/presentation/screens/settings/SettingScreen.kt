package com.example.culinair.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.culinair.presentation.components.CulinairButton
import com.example.culinair.presentation.dialogs.LogoutDialog
import com.example.culinair.presentation.viewmodel.settings.SettingsViewModel

/**
 * Created by John Ralph Dela Rosa on 8/9/2025.
 */
    @Composable
    fun SettingsScreen(
        viewModel: SettingsViewModel = hiltViewModel(),
        onLogoutClick: () -> Unit
    ) {
        val context = LocalContext.current
        var showLogoutLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.logoutState.collect { state ->
            when (state) {
                SettingsViewModel.LogoutState.Loading -> showLogoutLoading = true
                SettingsViewModel.LogoutState.Navigate -> {
                    showLogoutLoading = false
                    Toast.makeText(context, "👋 See you next time!", Toast.LENGTH_SHORT).show()
                    onLogoutClick()
                }

                SettingsViewModel.LogoutState.Success -> {
                    
                }

                SettingsViewModel.LogoutState.Error -> {

                }
            }
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2F4F4F),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.Start)
            )

            CulinairButton(
                text = "Logout",

                iconImage = Icons.Default.Logout,
                onClick = {
                    showLogoutLoading = true
                    viewModel.logout()
                }
            )

            if (showLogoutLoading) {
                LogoutDialog()
            }
        }

    }