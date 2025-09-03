package com.example.culinair.presentation.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.culinair.R
import com.example.culinair.domain.model.SocialLink
import com.example.culinair.domain.model.SocialPlatform
import com.example.culinair.presentation.components.CulinairCompactTextField
import com.example.culinair.presentation.dialogs.ErrorDialog
import com.example.culinair.presentation.screens.other_profile.EnhancedUserStatsCard
import com.example.culinair.presentation.screens.other_profile.RecipePostsSection
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.profile.ProfileViewModel
import com.example.culinair.presentation.viewmodel.recipe.RecipeViewModel

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    recipeViewModel: RecipeViewModel,
    onRecipeClick: (String) -> Unit
) {
    val avatarUrl = viewModel.avatarUrl
    val coverPhotoUrl = viewModel.coverPhotoUrl
    val isUploading = viewModel.isUploading
    val isUploadingCoverPhoto = viewModel.isUploadingCoverPhoto
    val uploadError = viewModel.uploadError
    val coverPhotoUploadError = viewModel.coverPhotoUploadError
    val isLoadingProfile = viewModel.isLoadingProfile
    val isSaving = viewModel.isSaving
    val saveError = viewModel.saveError
    val profileError = viewModel.profileError
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadCoverPhoto(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandGreen)
                        }
                    } else {
                        IconButton(onClick = { isEditMode = false }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = BrandGreen
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBackground)
            )
        }
    ) { innerPadding ->
        if (isLoadingProfile) {
            Box(modifier = Modifier.fillMaxSize().background(BrandBackground), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandBackground)
                    .padding(innerPadding)
            ) {
                // Cover photo with name + bio overlay
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable(enabled = isEditMode) { coverPickerLauncher.launch("image/*") }
                    ) {
                        if (isUploadingCoverPhoto) {
                            CircularProgressIndicator(color = BrandGreen)

                        } else {
                            AsyncImage(
                                model = coverPhotoUrl ?: "https://via.placeholder.com/600x200.png",
                                contentDescription = "Cover Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Gradient for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                    )
                                )
                        )

                        // Name + Bio overlay
                        if (!isEditMode) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = viewModel.displayName,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = viewModel.bio,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (isEditMode) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Cover",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(40.dp)
                            )
                        }
                    }
                }

                // Avatar
                item {
                    Box(
                        modifier = Modifier
                            .offset(y = (-50).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.3f))
                                .border(3.dp, Color.White, CircleShape)
                                .clickable(enabled = isEditMode && !isUploading) {
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(color = BrandGreen)
                            } else if (avatarUrl != null) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.DarkGray
                                )
                            }
                            if (isEditMode) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Cover",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(40.dp)
                                )
                            }
                        }


                    }
                }


                if (!isEditMode) {

                    // User Stats
                    item {
                        EnhancedUserStatsCard(
                            userStats = viewModel.userStats,
                            profileId = viewModel.profile?.id ?: "",
                            recipeViewModel = recipeViewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-30).dp)
                        )
                    }

                    // Social links
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .offset(y = (-30).dp)// Remove negative offset
                        ) {
                            Text(
                                "Socials",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreen,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            SocialLinks(
                                instagramUrl = viewModel.instagram.takeIf { it.isNotBlank() },
                                twitterUrl = viewModel.twitter.takeIf { it.isNotBlank() },
                                website = viewModel.website.takeIf { it.isNotBlank() }
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                                .offset(y = (-30).dp)
                        ) {
                            Text(
                                "Recipe Posts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreen,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            RecipePostsSection(
                                userId = viewModel.profile?.id ?: "",
                                recipeViewModel = recipeViewModel,
                                onRecipeClick = onRecipeClick,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    item { Spacer(Modifier.height(48.dp)) }
                } else {
                    // Editable Cards
                    item {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Personal Info",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                CulinairCompactTextField(
                                    viewModel.displayName,
                                    { viewModel.displayName = it },
                                    "Display Name"
                                )
                                Spacer(Modifier.height(8.dp))
                                CulinairCompactTextField(
                                    viewModel.bio,
                                    { viewModel.bio = it },
                                    "Bio"
                                )
                            }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Social Links", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(12.dp))
                                CulinairCompactTextField(
                                    viewModel.website,
                                    { viewModel.website = it },
                                    "Website"
                                )
                                Spacer(Modifier.height(8.dp))
                                CulinairCompactTextField(
                                    viewModel.instagram,
                                    { viewModel.instagram = it },
                                    "Instagram"
                                )
                                Spacer(Modifier.height(8.dp))
                                CulinairCompactTextField(
                                    viewModel.twitter,
                                    { viewModel.twitter = it },
                                    "Twitter"
                                )
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                viewModel.saveProfile()
                                isEditMode = false
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = Color.White,
                                disabledContentColor = Color.DarkGray,
                                disabledContainerColor = Color.Gray
                            )
                        ) {
                            Text(if (isSaving) "Saving..." else "Save")
                        }
                    }
                }

                item { Spacer(Modifier.height(48.dp)) }
            }
        }
    }

    // Error dialogs
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
        title = "Cover Photo Upload Error",
        message = coverPhotoUploadError ?: "",
        isVisible = coverPhotoUploadError != null,
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

@Composable
fun SocialLinks(
    instagramUrl: String? = null,
    twitterUrl: String? = null,
    website: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val socialLinks = listOfNotNull(
        instagramUrl?.let { SocialLink("Instagram", it, SocialPlatform.INSTAGRAM) },
        twitterUrl?.let { SocialLink("Twitter", it, SocialPlatform.TWITTER) },
        website?.let { SocialLink("Website", it, SocialPlatform.WEBSITE) }
    )

    if (socialLinks.isEmpty()) {
        // Empty state
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No social links added yet",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        // Social links using regular Column and Row layout
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (socialLinks.size) {
                1 -> {
                    // Single item - full width
                    SocialLinkCard(
                        socialLink = socialLinks[0],
                        onClick = { openUrl(context, socialLinks[0].url) }
                    )
                }

                2 -> {
                    // Two items - side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        socialLinks.forEach { socialLink ->
                            SocialLinkCard(
                                socialLink = socialLink,
                                onClick = { openUrl(context, socialLink.url) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                else -> {
                    // Three or more items - two per row
                    socialLinks.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { socialLink ->
                                SocialLinkCard(
                                    socialLink = socialLink,
                                    onClick = { openUrl(context, socialLink.url) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Add spacer if odd number of items in last row
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialLinkCard(
    socialLink: SocialLink,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = socialLink.platform.backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Platform icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (socialLink.platform) {
                    SocialPlatform.INSTAGRAM -> InstagramIcon()
                    SocialPlatform.TWITTER -> TwitterIcon()
                    SocialPlatform.WEBSITE -> WebsiteIcon()
                }
            }

            // Platform info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(0.dp),
                    text = socialLink.platform.displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    modifier = Modifier
                        .padding(0.dp),
                    text = socialLink.displayUrl,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Arrow icon
            Icon(
                Icons.Default.ArrowOutward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Custom brand icons using vector graphics
@Composable
fun InstagramIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_instagram),
        contentDescription = "Instagram",
        tint = Color.White,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun TwitterIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_twitter),
        contentDescription = "Twitter",
        tint = Color.White,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun WebsiteIcon(modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.Language,
        contentDescription = "Website",
        tint = Color.White,
        modifier = modifier.size(24.dp)
    )
}

fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG).show()
    }
}