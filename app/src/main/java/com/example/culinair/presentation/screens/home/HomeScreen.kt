package com.example.culinair.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.culinair.domain.model.RecipePreviewUiModel
import com.example.culinair.presentation.theme.BrandBackgroundYellow
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.home.HomeFilter
import com.example.culinair.presentation.viewmodel.home.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * Created by John Ralph Dela Rosa on 7/24/2025.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val recipes by viewModel.recipes.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            viewModel.loadHomeContent()
            isRefreshing = false
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackgroundYellow)
                .padding(16.dp)
        ) {
            item { HomeHeader({}, {}) }
            item { Spacer(Modifier.height(12.dp)) }
            item { FilterChips(selectedFilter, viewModel::selectFilter) }
            item { Spacer(Modifier.height(16.dp)) }

            items(recipes.size) { index ->
                val recipe = recipes[index]
                RecipeCard(
                    recipe = recipe,
                    onClick = { /* navigate to details */ },
                    onLike = { viewModel.likeRecipe(recipe.id) },
                    onSave = { viewModel.saveRecipe(recipe.id) }
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(
    onSearchQueryChange: (String) -> Unit,
    onNotificationClick: () -> Unit
) {
    val brandGreen = Color(0xFF2F4F4F) // Replace with your actual green

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = "",
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search...",
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = brandGreen
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = brandGreen
            ),
            textStyle = TextStyle(
                color = brandGreen,
                fontSize = 16.sp
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(
            onClick = onNotificationClick
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = brandGreen
            )
        }
    }
}

@Composable
fun FilterChips(selected: HomeFilter, onSelected: (HomeFilter) -> Unit) {
    val filters = HomeFilter.entries.toTypedArray()
    LazyRow {
        items(filters.size) { index ->
            val filter = filters[index]
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = {
                    Text(text = filter.name.replace("_", " ")) },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White, // Unselected background
                    selectedContainerColor = BrandGreen, // Selected background
                    labelColor = BrandGreen, // Text color when unselected
                    selectedLabelColor = Color.White // Text color when selected
                )
            )
        }
    }
}

@Composable
fun RecipeCard(
    recipe: RecipePreviewUiModel,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp)) {
                AsyncImage(
                    model = recipe.imageUrl?.ifBlank { "https://via.placeholder.com/400x300?text=No+Image" },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // or whatever fits your card
                )

                IconButton(
                    onClick = onSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save")
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = recipe.avatarUrl?.ifBlank { "https://via.placeholder.com/400x300?text=No+Image" },
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(recipe.displayName, fontWeight = FontWeight.SemiBold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text("4.5", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text(recipe.title, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${recipe.cookTimeMinutes} min",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(recipe.difficulty, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLike, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = if(recipe.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (recipe.isLikedByCurrentUser) Color.Red else Color.Gray
                                )
                        }
                        Text("${recipe.likes}", fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("${recipe.comments}", fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Saved",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Save", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
