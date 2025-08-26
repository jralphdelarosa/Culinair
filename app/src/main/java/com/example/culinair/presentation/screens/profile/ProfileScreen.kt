package com.example.culinair.presentation.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.culinair.presentation.components.CulinairCompactTextField
import com.example.culinair.presentation.components.CulinairButton
import com.example.culinair.presentation.dialogs.CircularLogoWithLoadingRing
import com.example.culinair.presentation.dialogs.ErrorDialog
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.profile.ProfileViewModel

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
) {
    // ViewModel state
    val avatarUrl = viewModel.avatarUrl
    val isUploading = viewModel.isUploading
    val uploadError = viewModel.uploadError
    val isLoadingProfile = viewModel.isLoadingProfile
    val isSaving = viewModel.isSaving
    val saveSuccess = viewModel.saveSuccess
    val saveError = viewModel.saveError
    val profileError = viewModel.profileError

    val context = LocalContext.current

    // Show success message
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            viewModel.clearErrors()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadAvatar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBackground   // Optional: Text color
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .background(BrandBackground)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoadingProfile) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularLogoWithLoadingRing()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading profile...",
                            color = Color(0xFF2F4F4F),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .clickable(enabled = !isUploading) { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isUploading -> CircularLogoWithLoadingRing()

                            avatarUrl != null -> AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            else -> Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Select Avatar",
                                modifier = Modifier.size(56.dp),
                                tint = Color(0xFF2F4F4F)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isUploading) "Uploading avatar..." else "Tap your avatar to upload a new profile picture.",
                        fontSize = 14.sp,
                        color = Color(0xFF2F4F4F)
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    CulinairCompactTextField(
                        value = viewModel.displayName,
                        onValueChange = { viewModel.displayName = it },
                        label = "Display Name"
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    CulinairCompactTextField(
                        value = viewModel.bio,
                        onValueChange = { viewModel.bio = it },
                        label = "Bio"
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    CulinairCompactTextField(
                        value = viewModel.website,
                        onValueChange = { viewModel.website = it },
                        label = "Website"
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    CulinairCompactTextField(
                        value = viewModel.instagram,
                        onValueChange = { viewModel.instagram = it },
                        label = "Instagram"
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    CulinairCompactTextField(
                        value = viewModel.twitter,
                        onValueChange = { viewModel.twitter = it },
                        label = "Twitter"
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    CulinairButton(
                        text = if (isSaving) "Saving..." else "Save",
                        onClick = { viewModel.saveProfile() },
                        enabled = !isSaving && viewModel.hasUnsavedChanges
                    )
                }
            }

            // Extra space at bottom
            item { Spacer(modifier = Modifier.height(48.dp)) }
        }
    }

    // Error Dialogs - NO SPACE TAKEN FROM UI
    ErrorDialog(
        title = "Profile Load Error",
        message = profileError ?: "",
        isVisible = profileError != null,
        onDismiss = { viewModel.clearErrors() },
        onRetry = { viewModel.loadProfile() }
    )

    ErrorDialog(
        title = "Avatar Upload Error",
        message = uploadError ?: "",
        isVisible = uploadError != null,
        onDismiss = { viewModel.clearErrors() }
    )

    ErrorDialog(
        title = "Save Error",
        message = saveError ?: "",
        isVisible = saveError != null,
        onDismiss = { viewModel.clearErrors() },
        onRetry = { viewModel.saveProfile() }
    )
}