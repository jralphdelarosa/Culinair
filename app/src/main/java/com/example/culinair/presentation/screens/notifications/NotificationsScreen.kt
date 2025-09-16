package com.example.culinair.presentation.screens.notifications

import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.culinair.R
import com.example.culinair.data.remote.model.response.NotificationType
import com.example.culinair.domain.model.NotificationUIModel
import com.example.culinair.presentation.theme.AppStandardYellow
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandBlue
import com.example.culinair.presentation.theme.BrandGold
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.theme.BrandRed
import com.example.culinair.presentation.viewmodel.notifications.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    navHostController: NavHostController,
    onRecipeNotificationClick: (String) -> Unit
) {
    val notifications = viewModel.notifications
    val isLoading = viewModel.isLoading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.loadNotifications() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BrandBackground,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = BrandGreen
                    )

                    if (notifications.isNotEmpty()) {
                        val unreadCount = notifications.count { !it.isRead }
                        Text(
                            text = if (unreadCount > 0) "$unreadCount new notifications" else "All caught up!",
                            style = MaterialTheme.typography.body2,
                            color = if (unreadCount > 0) BrandGold else Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            if (notifications.isEmpty() && !isLoading) {
                EmptyNotificationsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications.size, key = { index -> notifications[index].id }) { index ->
                        val notification = notifications[index]
                        val dismissState = rememberDismissState(
                            confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart) {
                                    viewModel.deleteNotification(notification.id)
                                    true
                                } else false
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            background = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.Transparent
                                        DismissValue.DismissedToStart -> BrandRed
                                        else -> Color.Transparent
                                    },
                                    label = "background_color"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (dismissState.targetValue == DismissValue.DismissedToStart) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = "Delete",
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            },
                            directions = setOf(DismissDirection.EndToStart),
                            dismissContent = {
                                NotificationItem(
                                    notification = notification,
                                    onClick = {
                                        Log.d("NotificationScreen", "Notification clicked: ${notification.id}")
                                        viewModel.markAsRead(notification.id)
                                        when(notification.type){
                                            NotificationType.LIKE.toString() -> {
                                                onRecipeNotificationClick(notification.recipeId.toString())
                                            }
                                            NotificationType.SAVE.toString() -> {
                                                onRecipeNotificationClick(notification.recipeId.toString())
                                            }
                                            NotificationType.FOLLOW.toString() -> {
                                                navHostController.navigate("other_profile/${notification.actorId}")
                                            }
                                            NotificationType.COMMENT.toString() -> {
                                                onRecipeNotificationClick(notification.recipeId.toString())
                                            }
                                            NotificationType.SYSTEM.toString() -> {
                                                navHostController.navigate("home")
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }

                    // Add some bottom padding for the last item
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = BrandGreen,
            backgroundColor = Color.White
        )
    }
}

@Composable
fun NotificationItem(
    notification: NotificationUIModel,
    onClick: () -> Unit
) {
    val backgroundColor = if (notification.isRead) Color.White else BrandGold.copy(alpha = 0.08f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar with status indicator
            Box {
                AsyncImage(
                    model = notification.actorAvatar ?: "",
                    contentDescription = "Actor avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_person_placeholder),
                    error = painterResource(id = R.drawable.ic_person_placeholder)
                )

                // Notification type indicator
                val iconColor = when (notification.type) {
                    "FOLLOW" -> BrandGreen
                    "LIKE" -> Color.Red
                    "COMMENT" -> BrandBlue
                    "SAVE" -> AppStandardYellow
                    else -> Color.Gray
                }

                val iconVector = when (notification.type) {
                    "FOLLOW" -> Icons.Default.PersonAdd
                    "LIKE" -> Icons.Default.Favorite
                    "COMMENT" -> Icons.Default.Comment
                    "SAVE" -> Icons.Default.BookmarkAdded
                    else -> Icons.Default.Notifications
                }

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(iconColor, CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Notification message with rich text
                val message = buildAnnotatedString {
                    val actorName = notification.actorName
                    val baseText = when (notification.type) {
                        "FOLLOW" -> "$actorName started following you"
                        "LIKE" -> "$actorName liked your recipe"
                        "COMMENT" -> "$actorName commented on your recipe"
                        "SAVE" -> "$actorName saved your recipe"
                        "SYSTEM" -> notification.message
                        else -> "Notification"
                    }

                    // Make actor name bold
                    val startIndex = baseText.indexOf(actorName)
                    if (startIndex != -1) {
                        append(baseText.substring(0, startIndex))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = BrandGreen)) {
                            append(actorName)
                        }
                        append(baseText.substring(startIndex + actorName.length))
                    } else {
                        append(baseText)
                    }
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    ),
                    color = BrandGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time with modern styling
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatTimeAgo(notification.createdAt),
                        style = MaterialTheme.typography.caption.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )

                    // Unread indicator dot
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(BrandGold, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Modern illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    BrandGreen.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = BrandGreen.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No notifications yet",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            ),
            color = BrandGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "When you get notifications, they'll show up here",
            style = MaterialTheme.typography.body2,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

fun formatTimeAgo(createdAt: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")

        val date = format.parse(createdAt) ?: return "unknown"
        val now = Date()

        val diffInMillis = now.time - date.time
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

        when {
            diffInMinutes < 1 -> "just now"
            diffInMinutes < 60 -> "${diffInMinutes}m"
            diffInMinutes < 1440 -> "${diffInMinutes / 60}h"
            diffInMinutes < 10080 -> "${diffInMinutes / 1440}d"
            else -> {
                val weeks = diffInMinutes / 10080
                if (weeks < 4) "${weeks}w" else "1mo+"
            }
        }
    } catch (e: Exception) {
        "unknown"
    }
}

