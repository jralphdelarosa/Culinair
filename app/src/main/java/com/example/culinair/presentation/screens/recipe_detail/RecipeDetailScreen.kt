package com.example.culinair.presentation.screens.recipe_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.culinair.R
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.presentation.LikeAnimation
import com.example.culinair.presentation.SaveAnimation
import com.example.culinair.presentation.theme.AppStandardYellow
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGreen
import com.example.culinair.presentation.viewmodel.profile.ProfileViewModel
import com.example.culinair.presentation.viewmodel.recipe.RecipeViewModel

/**
 * Created by John Ralph Dela Rosa on 8/22/2025.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    recipeId: String,
    navController: NavController
) {
    val recipes by recipeViewModel.allPublicRecipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId }

    // Get current user profile data
    val profile by remember { derivedStateOf { profileViewModel.profile } }
    val currentUserAvatar = profile?.avatarUrl

    var showLikeAnimation by remember { mutableStateOf(false) }
    var showSaveAnimation by remember { mutableStateOf(false) }

    val sampleTags = listOf(
        "Easy",
        "Quick",
        "Vegetarian",
        "Gluten-Free",
        "Spicy",
        "Healthy",
        "Dessert",
        "Low-Carb",
        "Kid-Friendly",
        "Vegan",
        "Comfort Food",
        "Under 30 mins"
    )

    LaunchedEffect(recipeId) {
        recipeViewModel.loadComments(recipeId)
    }

    if (recipe == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Recipe not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = BrandGreen, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBackground   // Optional: Text color
                )
            )
        },
        modifier = Modifier.background(BrandBackground)

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .background(BrandBackground)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            /** HERO IMAGE WITH OVERLAY **/
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )

                    // TAGS
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleTags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tag, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Title and Author
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            recipe.title,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = recipe.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "by ${recipe.displayName}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Floating Like & Save Buttons
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (!recipe.isLikedByCurrentUser) showLikeAnimation = true
                                recipeViewModel.likeRecipe(recipe.id)
                            },
                            containerColor = Color.White.copy(alpha = 0.6f),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = if (recipe.isLikedByCurrentUser) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (recipe.isLikedByCurrentUser) Color.Red else Color.Black
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                if (!recipe.isSavedByCurrentUser) showSaveAnimation = true
                                recipeViewModel.saveRecipe(recipe.id)
                            },
                            containerColor = Color.White.copy(alpha = 0.6f),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = if (recipe.isSavedByCurrentUser) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (recipe.isSavedByCurrentUser) AppStandardYellow else Color.Black
                            )
                        }
                    }
                    LikeAnimation(
                        triggerAnimation = showLikeAnimation,
                        onAnimationEnd = { showLikeAnimation = false })
                    SaveAnimation(
                        triggerAnimation = showSaveAnimation,
                        onAnimationEnd = { showSaveAnimation = false })
                }
            }

            /** QUICK STATS ROW **/
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(icon = Icons.Default.Schedule, text = "${recipe.cookTimeMinutes} min")
                    StatChip(icon = Icons.Default.Whatshot, text = recipe.difficulty)
                    StatChip(icon = Icons.Default.Favorite, text = "${recipe.likesCount}")
                    StatChip(icon = Icons.Default.Comment, text = "${recipe.commentsCount}")
                }
            }

            /** DESCRIPTION **/
            item {
                Text(
                    recipe.description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            /** INGREDIENTS **/
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    CollapsibleSection(title = "Ingredients", items = recipe.ingredients)
                }
            }

            /** STEPS **/
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    CollapsibleSection(title = "Steps", items = recipe.steps)
                }
            }

            /** COMMENTS SECTION **/
            item {
                CommentsSection(
                    comments = recipeViewModel.comments.collectAsState().value,
                    onAddComment = { content, parentId ->
                        recipeViewModel.addComment(
                            recipeId = recipeId,
                            parentCommentId = parentId,
                            content = content
                        )
                    },
                    isAddingComment = recipeViewModel.isAddingComment.collectAsState().value,
                    commentError = recipeViewModel.commentError.collectAsState().value,
                    onClearError = { recipeViewModel.clearCommentError() },
                    currentUserAvatar = currentUserAvatar
                )
            }
        }

    }
}

@Composable
fun StatChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(BrandGreen.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CommentsSection(
    comments: List<CommentUiModel>,
    onAddComment: (String, String?) -> Unit,
    isAddingComment: Boolean = false,
    commentError: String? = null,
    onClearError: () -> Unit = {},
    currentUserAvatar: String? = null
) {
    var newComment by remember { mutableStateOf("") }
    var replyToCommentId by remember { mutableStateOf<String?>(null) }
    var replyToDisplayName by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Comments (${comments.size})",
            style = MaterialTheme.typography.titleMedium,
            color = BrandGreen,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        // Error message
        commentError?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearError) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Enhanced comment input
        EnhancedCommentInput(
            value = newComment,
            onValueChange = { newComment = it },
            onSubmit = {
                if (newComment.isNotBlank()) {
                    onAddComment(newComment, replyToCommentId)
                    newComment = ""
                    replyToCommentId = null
                    replyToDisplayName = null
                }
            },
            isLoading = isAddingComment,
            isReply = replyToCommentId != null,
            replyToName = replyToDisplayName,
            onCancelReply = {
                replyToCommentId = null
                replyToDisplayName = null
                newComment = ""
            },
            userAvatarUrl = currentUserAvatar,
            placeholder = if (replyToCommentId != null) "Write a reply..." else "Share your thoughts..."
        )

        Spacer(Modifier.height(20.dp))

        if (comments.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No comments yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Be the first to share your thoughts!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            comments.forEach { comment ->
                CommentItem(
                    comment = comment,
                    depth = 0,
                    onReplyClick = { commentId ->
                        replyToCommentId = commentId
                        replyToDisplayName = findCommentById(comments, commentId)?.displayName
                    }
                )
            }
        }
    }
}

// Helper function to find comment by ID (including nested comments)
fun findCommentById(comments: List<CommentUiModel>, commentId: String): CommentUiModel? {
    comments.forEach { comment ->
        if (comment.id == commentId) return comment

        val found = findCommentById(comment.children, commentId)
        if (found != null) return found
    }
    return null
}

@Composable
fun CommentItem(
    comment: CommentUiModel,
    depth: Int,
    onReplyClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val maxDepth = 3
    val actualDepth = minOf(depth, maxDepth)

    Column(
        modifier = Modifier
            .padding(start = (actualDepth * 16).dp, top = 8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = comment.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(comment.displayName, fontWeight = FontWeight.Bold)
                Text(
                    text = comment.createdAt.take(10),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Collapse/Expand button for comments with children
            if (comment.children.isNotEmpty()) {
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse replies" else "Expand replies",
                        tint = BrandGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Show reply count when collapsed
                if (!isExpanded) {
                    Text(
                        text = "${comment.children.size}",
                        fontSize = 10.sp,
                        color = BrandGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = comment.content,
            modifier = Modifier.padding(start = 38.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.padding(start = 38.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    val replyToId = if (depth >= maxDepth) {
                        comment.id // Keep it simple for now
                    } else {
                        comment.id
                    }
                    onReplyClick(replyToId)
                }
            ) {
                Text("Reply", color = BrandGreen, fontSize = 12.sp)
            }

            // Show collapse/expand text button as alternative
            if (comment.children.isNotEmpty()) {
                TextButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Text(
                        text = if (isExpanded) "Hide replies" else "Show ${comment.children.size} replies",
                        color = BrandGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Render children with animation
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
        ) {
            Column {
                comment.children.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        depth = depth + 1,
                        onReplyClick = onReplyClick
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedCommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean = false,
    isReply: Boolean = false,
    replyToName: String? = null,
    onCancelReply: () -> Unit = {},
    userAvatarUrl: String? = null,
    placeholder: String = "Write a comment...",
    maxLength: Int = 500
) {
    var isFocused by remember { mutableStateOf(false) }
    val isOverLimit = value.length > maxLength
    val canSubmit = value.isNotBlank() && !isOverLimit && !isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isReply) BrandGreen.copy(alpha = 0.05f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(if (isReply) 12.dp else 0.dp)
    ) {

        // Reply context banner
        if (isReply && replyToName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        BrandGreen.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Reply,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Replying to $replyToName",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGreen,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCancelReply,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancel Reply",
                        tint = BrandGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Main input area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // User avatar
            AsyncImage(
                model = userAvatarUrl,
                contentDescription = "Your avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(alpha = 0.1f)),
                placeholder = painterResource(R.drawable.ic_person_placeholder), // Add default avatar
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Input field and actions
            Column(modifier = Modifier.weight(1f)) {

                // Text input
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    shape = RoundedCornerShape(20.dp),
                    minLines = if (isFocused || value.isNotEmpty()) 3 else 1,
                    maxLines = 6,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        errorContainerColor = if (isOverLimit) Color.Red.copy(alpha = 0.1f) else Color.White
                    ),
                    isError = isOverLimit
                )

                // Bottom row with character count and submit button
                AnimatedVisibility(
                    visible = isFocused || value.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Character count
                        Text(
                            text = "${value.length}/$maxLength",
                            fontSize = 12.sp,
                            color = if (isOverLimit) Color.Red else Color.Gray
                        )

                        // Submit button
                        Button(
                            onClick = onSubmit,
                            enabled = canSubmit,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Text("Posting...")
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(if (isReply) "Reply" else "Post")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Error message for character limit
        AnimatedVisibility(visible = isOverLimit) {
            Text(
                text = "Comment is too long. Please keep it under $maxLength characters.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 48.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun CollapsibleSection(title: String, items: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = BrandGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                items.forEach {
                    Text(
                        "• $it",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}