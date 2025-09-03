package com.example.culinair.presentation.screens.other_profile

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.culinair.data.remote.dto.response.ProfileResponse
import com.example.culinair.domain.model.RecipeDetailUiModel
import com.example.culinair.domain.model.UserStats
import com.example.culinair.presentation.screens.profile.SocialLinks
import com.example.culinair.presentation.theme.AppStandardYellow
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.other_profile.OtherProfileViewModel
import com.example.culinair.presentation.viewmodel.profile.ProfileViewModel
import com.example.culinair.presentation.viewmodel.recipe.RecipeViewModel

/**
 * Created by John Ralph Dela Rosa on 8/29/2025.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfileScreen(
    userId: String,
    viewModel: OtherProfileViewModel = hiltViewModel(),
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.loadProfileById(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.profile?.displayName ?: "Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            "Share profile feature coming soon",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Profile",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBackground)
            )
        }
    ) { innerPadding ->
        when {
            viewModel.isLoadingProfile -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            viewModel.profileError != null -> {
                ErrorState(
                    error = viewModel.profileError!!,
                    onRetry = { viewModel.loadProfileById(userId) },
                    onBack = onBack
                )
            }

            viewModel.profile != null -> {
                ProfileContent(
                    profile = viewModel.profile!!,
                    userStats = viewModel.userStats,
                    isFollowActionInProgress = viewModel.isFollowActionInProgress,
                    onFollowToggle = { viewModel.toggleFollow(userId) },
                    recipeViewModel = recipeViewModel,
                    onRecipeClick = onRecipeClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileResponse,
    userStats: UserStats,
    isFollowActionInProgress: Boolean,
    onFollowToggle: () -> Unit,
    recipeViewModel: RecipeViewModel, // Add this parameter
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        // Cover photo with name + bio overlay (KEEPING EXACTLY AS IS)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = profile.coverPhotoUrl ?: "https://via.placeholder.com/600x200.png",
                    contentDescription = "Cover Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

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
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = (-10).dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = profile.displayName ?: "Unknown",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (profile.bio?.isNotBlank() == true) {
                        Text(
                            text = profile.bio,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier.offset(y = (-50).dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile.avatarUrl != null) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }

        // Follow Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-60).dp),
                contentAlignment = Alignment.Center
            ) {
                FollowButton(
                    isFollowing = userStats.isFollowing,
                    isLoading = isFollowActionInProgress,
                    onClick = onFollowToggle
                )
            }
        }

        // Enhanced User Stats
        item {
            EnhancedUserStatsCard(
                userStats = userStats,
                profileId = profile.id ?: "",
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
                    .offset(y = (-10).dp)
            ) {
                Text(
                    "Social Links",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                SocialLinks(
                    instagramUrl = profile.instagram.takeIf { it?.isNotBlank() == true },
                    twitterUrl = profile.twitter.takeIf { it?.isNotBlank() == true },
                    website = profile.website.takeIf { it?.isNotBlank() == true }
                )
            }
        }

        // Recipe Posts Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(
                    "Recipe Posts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                RecipePostsSection(
                    userId = profile.id ?: "",
                    recipeViewModel = recipeViewModel,
                    onRecipeClick = onRecipeClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item { Spacer(Modifier.height(48.dp)) }
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isFollowing) Color.White else BrandGreen,
        animationSpec = tween(durationMillis = 300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isFollowing) BrandGreen else Color.White,
        animationSpec = tween(durationMillis = 300)
    )

    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .height(40.dp)
            .widthIn(min = 120.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.6f)
        ),
        border = if (isFollowing) BorderStroke(1.dp, BrandGreen) else null,
        shape = RoundedCornerShape(20.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isFollowing) "Unfollow" else "Follow",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EnhancedUserStatsCard(
    userStats: UserStats,
    profileId: String,
    recipeViewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val allRecipes by recipeViewModel.allPublicRecipes.collectAsState()
    val userRecipeCount = remember(allRecipes, profileId) {
        allRecipes.count { it.userId == profileId }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            BrandGreen.copy(alpha = 0.08f),
                            BrandGreen.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EnhancedStatItem(
                label = "Recipes",
                value = userRecipeCount,
                icon = Icons.Default.Restaurant
            )

            VerticalDivider()

            EnhancedStatItem(
                label = "Followers",
                value = userStats.followersCount,
                icon = Icons.Default.People
            )

            VerticalDivider()

            EnhancedStatItem(
                label = "Following",
                value = userStats.followingCount,
                icon = Icons.Default.PersonAdd
            )
        }
    }
}

@Composable
private fun EnhancedStatItem(
    label: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    BrandGreen.copy(alpha = 0.12f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandGreen,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(50.dp)
            .width(1.dp)
            .background(Color.Gray.copy(alpha = 0.2f))
    )
}

@Composable
fun RecipePostsSection(
    userId: String,
    recipeViewModel: RecipeViewModel,
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allRecipes by recipeViewModel.allPublicRecipes.collectAsState()
    val userRecipes = remember(allRecipes, userId) {
        allRecipes.filter { it.userId == userId }
    }

    if (userRecipes.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = BrandGreen.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No Recipe Posts Yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandGreen
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "This user hasn't shared any recipes yet",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            userRecipes.forEach { recipe ->
                CompactRecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe.id) },
                    onLike = { recipeViewModel.likeRecipe(recipe.id, recipe.userId.toString()) },
                    onSave = { recipeViewModel.saveRecipe(recipe.id, recipe.userId.toString()) }
                )
            }
        }
    }
}

@Composable
private fun CompactRecipeCard(
    recipe: RecipeDetailUiModel,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe Image
            AsyncImage(
                model = recipe.imageUrl.ifBlank { "https://via.placeholder.com/400x300?text=No+Image" },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(12.dp))

            // Recipe Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${recipe.cookTimeMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(Modifier.width(12.dp))

                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        recipe.difficulty,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (recipe.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (recipe.isLikedByCurrentUser) Color.Red else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${recipe.likesCount}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${recipe.commentsCount}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (recipe.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (recipe.isLikedByCurrentUser) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onSave,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (recipe.isSavedByCurrentUser) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (recipe.isSavedByCurrentUser) AppStandardYellow else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Unable to load profile",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = error,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandGreen
                )
            ) {
                Text("Go Back")
            }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Try Again")
            }
        }
    }
}


