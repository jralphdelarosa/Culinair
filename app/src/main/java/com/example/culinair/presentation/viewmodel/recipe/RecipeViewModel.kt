package com.example.culinair.presentation.viewmodel.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.model.RecipeDetailUiModel
import com.example.culinair.domain.usecase.auth.RestoreSessionUseCase
import com.example.culinair.domain.usecase.comment.AddCommentUseCase
import com.example.culinair.domain.usecase.comment.GetCommentsUseCase
import com.example.culinair.domain.usecase.home.GetAllPublicRecipesUseCase
import com.example.culinair.domain.usecase.home.GetFollowingRecipesUseCase
import com.example.culinair.domain.usecase.home.GetRecommendedRecipesUseCase
import com.example.culinair.domain.usecase.home.GetTrendingRecipesUseCase
import com.example.culinair.domain.usecase.home.LikeRecipeUseCase
import com.example.culinair.domain.usecase.home.SaveRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getAllPublicRecipesUseCase: GetAllPublicRecipesUseCase,
    private val getFollowingRecipesUseCase: GetFollowingRecipesUseCase,
    private val getTrendingRecipesUseCase: GetTrendingRecipesUseCase,
    private val getRecommendedRecipesUseCase: GetRecommendedRecipesUseCase,
    private val likeRecipeUseCase: LikeRecipeUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val restoreSessionUseCase: RestoreSessionUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "RecipeViewModel"
    }

    private val _allPublicRecipes = MutableStateFlow<List<RecipeDetailUiModel>>(emptyList())
    val allPublicRecipes: StateFlow<List<RecipeDetailUiModel>> = _allPublicRecipes

    private val _followingRecipes = MutableStateFlow<List<RecipeDetailUiModel>>(emptyList())
    val followingRecipes: StateFlow<List<RecipeDetailUiModel>> = _followingRecipes

    private val _savedRecipes = MutableStateFlow<List<RecipeDetailUiModel>>(emptyList())
    val savedRecipes: StateFlow<List<RecipeDetailUiModel>> = _savedRecipes

    private val _trendingRecipes = MutableStateFlow<List<RecipeDetailUiModel>>(emptyList())
    val trendingRecipes: StateFlow<List<RecipeDetailUiModel>> = _trendingRecipes

    private val _recommendedRecipes = MutableStateFlow<List<RecipeDetailUiModel>>(emptyList())
    val recommendedRecipes: StateFlow<List<RecipeDetailUiModel>> = _recommendedRecipes

    private val _comments = MutableStateFlow<List<CommentUiModel>>(emptyList())
    val comments: StateFlow<List<CommentUiModel>> = _comments

    private val _isAddingComment = MutableStateFlow(false)
    val isAddingComment = _isAddingComment.asStateFlow()

    private val _commentError = MutableStateFlow<String?>(null)
    val commentError = _commentError.asStateFlow()

    private val _selectedFilter = MutableStateFlow(HomeFilter.ALL)
    val selectedFilter: StateFlow<HomeFilter> = _selectedFilter

    val recipes: StateFlow<List<RecipeDetailUiModel>> =
        combine(
            selectedFilter,
            followingRecipes,
            trendingRecipes,
            savedRecipes,
            allPublicRecipes
        ) { filter, following, trending, saved, allPublic ->
            Log.d(TAG, "Combining recipes for filter: $filter")
            Log.d(TAG, "Recipe counts - Following: ${following.size}, Trending: ${trending.size}, Saved: ${saved.size}, All: ${allPublic.size}")

            when (filter) {
                HomeFilter.ALL -> {
                    Log.d(TAG, "Returning all public recipes: ${allPublic.size}")
                    allPublic
                }
                HomeFilter.FOLLOWING -> {
                    Log.d(TAG, "Returning following recipes: ${following.size}")
                    following
                }
                HomeFilter.TRENDING -> {
                    Log.d(TAG, "Returning trending recipes: ${trending.size}")
                    trending
                }
                HomeFilter.SAVED -> {
                    Log.d(TAG, "Returning saved recipes: ${saved.size}")
                    saved
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        Log.d(TAG, "RecipeViewModel initialized")
        loadHomeContent()
    }

    fun selectFilter(filter: HomeFilter) {
        Log.d(TAG, "Filter selected: ${filter.name}")
        _selectedFilter.value = filter
    }

    fun loadHomeContent() {
        Log.d(TAG, "Starting to load home content")
        viewModelScope.launch {
            try {
                Log.d(TAG, "Restoring session")
                restoreSessionUseCase()

                val token = sessionManager.getAccessToken()
                val userId = sessionManager.getUserId()

                if (token == null) {
                    Log.e(TAG, "Access token is null - cannot load recipes")
                    return@launch
                }

                if (userId == null) {
                    Log.e(TAG, "User ID is null - cannot load recipes")
                    return@launch
                }

                Log.d(TAG, "Loading recipes with userId: $userId")

                // Load all public recipes
                Log.d(TAG, "Loading all public recipes")
                val allPublic = getAllPublicRecipesUseCase(userId, token)
                Log.d(TAG, "Loaded ${allPublic.size} public recipes")
                _allPublicRecipes.value = allPublic

                // Load following recipes
                Log.d(TAG, "Loading following recipes")
                val following = getFollowingRecipesUseCase(userId, token)
                Log.d(TAG, "Loaded ${following.size} following recipes")
                _followingRecipes.value = following

                // Load trending recipes
                Log.d(TAG, "Loading trending recipes")
                val trending = getTrendingRecipesUseCase(userId, token)
                Log.d(TAG, "Loaded ${trending.size} trending recipes")
                _trendingRecipes.value = trending

                // Load recommended recipes
                Log.d(TAG, "Loading recommended recipes")
                val recommended = getRecommendedRecipesUseCase(userId, token)
                Log.d(TAG, "Loaded ${recommended.size} recommended recipes")
                _recommendedRecipes.value = recommended

                Log.d(TAG, "Updating saved recipes")
                updateSavedRecipes()

                Log.d(TAG, "Home content loading completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading home content", e)
            }
        }
    }

    private fun updateSavedRecipes() {
        Log.d(TAG, "Updating saved recipes from ${_allPublicRecipes.value.size} total recipes")

        val savedRecipes = _allPublicRecipes.value.filter { recipe ->
            recipe.isSavedByCurrentUser
        }

        Log.d(TAG, "Found ${savedRecipes.size} saved recipes")
        _savedRecipes.value = savedRecipes
    }

    fun likeRecipe(recipeId: String, recipeOwner: String) {
        Log.d(TAG, "Attempting to like recipe: $recipeId")
        viewModelScope.launch {
            try {
                val token = sessionManager.getAccessToken()
                val userId = sessionManager.getUserId()

                if (token == null) {
                    Log.e(TAG, "Cannot like recipe - access token is null")
                    return@launch
                }

                if (userId == null) {
                    Log.e(TAG, "Cannot like recipe - user ID is null")
                    return@launch
                }

                Log.d(TAG, "Calling like recipe use case for recipe: $recipeId")
                val result = likeRecipeUseCase(recipeId, userId, token, recipeOwner)

                result?.let { likeResponse ->
                    Log.d(TAG, "Like recipe successful - liked: ${likeResponse.liked}, new count: ${likeResponse.likesCount}")
                    updateRecipeLikeStatus(recipeId, likeResponse.liked, likeResponse.likesCount)
                } ?: run {
                    Log.w(TAG, "Like recipe response was null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error liking recipe: $recipeId", e)
            }
        }
    }

    private fun updateRecipeLikeStatus(recipeId: String, isLiked: Boolean, newLikesCount: Int) {
        Log.d(TAG, "Updating like status for recipe: $recipeId, liked: $isLiked, count: $newLikesCount")

        // Update all recipe lists
        val originalAllCount = _allPublicRecipes.value.size
        _allPublicRecipes.value = _allPublicRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe
        }
        Log.d(TAG, "Updated all public recipes (${originalAllCount} items)")

        val originalFollowingCount = _followingRecipes.value.size
        _followingRecipes.value = _followingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe
        }
        Log.d(TAG, "Updated following recipes (${originalFollowingCount} items)")

        val originalTrendingCount = _trendingRecipes.value.size
        _trendingRecipes.value = _trendingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe
        }
        Log.d(TAG, "Updated trending recipes (${originalTrendingCount} items)")
    }

    private fun updateRecipeSaveStatus(recipeId: String, isSaved: Boolean, newSavesCount: Int) {
        Log.d(TAG, "Updating save status for recipe: $recipeId, saved: $isSaved, count: $newSavesCount")

        // Update all recipe lists
        val originalAllCount = _allPublicRecipes.value.size
        _allPublicRecipes.value = _allPublicRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe
        }
        Log.d(TAG, "Updated all public recipes save status (${originalAllCount} items)")

        val originalFollowingCount = _followingRecipes.value.size
        _followingRecipes.value = _followingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe
        }
        Log.d(TAG, "Updated following recipes save status (${originalFollowingCount} items)")

        val originalTrendingCount = _trendingRecipes.value.size
        _trendingRecipes.value = _trendingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe
        }
        Log.d(TAG, "Updated trending recipes save status (${originalTrendingCount} items)")

        // Update saved recipes list
        updateSavedRecipes()
    }

    fun saveRecipe(recipeId: String, recipeOwner: String) {
        Log.d(TAG, "Attempting to save recipe: $recipeId")
        viewModelScope.launch {
            try {
                val token = sessionManager.getAccessToken()
                val userId = sessionManager.getUserId()

                if (token == null) {
                    Log.e(TAG, "Cannot save recipe - access token is null")
                    return@launch
                }

                if (userId == null) {
                    Log.e(TAG, "Cannot save recipe - user ID is null")
                    return@launch
                }

                Log.d(TAG, "Calling save recipe use case for recipe: $recipeId")
                val result = saveRecipeUseCase(recipeId, userId, token, recipeOwner)

                result?.let { saveResponse ->
                    Log.d(TAG, "Save recipe successful - saved: ${saveResponse.saved}, new count: ${saveResponse.savesCount}")
                    updateRecipeSaveStatus(recipeId, saveResponse.saved, saveResponse.savesCount)
                } ?: run {
                    Log.w(TAG, "Save recipe response was null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving recipe: $recipeId", e)
            }
        }
    }

    fun addComment(recipeId: String, parentCommentId: String? = null, content: String, recipeOwner: String) {
        Log.d(TAG, "Adding comment to recipe: $recipeId, parent: $parentCommentId, content length: ${content.length}")
        viewModelScope.launch {
            _isAddingComment.value = true
            _commentError.value = null

            try {
                val token = sessionManager.getAccessToken()
                val userId = sessionManager.getUserId()

                if (token == null) {
                    Log.e(TAG, "Cannot add comment - access token is null")
                    _commentError.value = "Authentication error"
                    return@launch
                }

                if (userId == null) {
                    Log.e(TAG, "Cannot add comment - user ID is null")
                    _commentError.value = "Authentication error"
                    return@launch
                }

                Log.d(TAG, "Calling add comment use case")
                val result = addCommentUseCase(token, userId, recipeId, content, parentCommentId, recipeOwner)

                result?.let {
                    Log.d(TAG, "Comment added successfully, new comments count: ${it.commentsCount}")
                    updateRecipeCommentsCount(recipeId, it.commentsCount)

                    // Refresh comments for accuracy
                    Log.d(TAG, "Refreshing comments list")
                    loadComments(recipeId)
                } ?: run {
                    Log.w(TAG, "Add comment response was null")
                    _commentError.value = "Failed to add comment"
                }
            } catch (e: Exception) {
                val errorMessage = "Error posting comment: ${e.message}"
                Log.e(TAG, errorMessage, e)
                _commentError.value = errorMessage
            } finally {
                Log.d(TAG, "Setting isAddingComment to false")
                _isAddingComment.value = false
            }
        }
    }

    private fun updateRecipeCommentsCount(recipeId: String, newCount: Int) {
        Log.d(TAG, "Updating comments count for recipe: $recipeId, new count: $newCount")

        _allPublicRecipes.value = _allPublicRecipes.value.map {
            if (it.id == recipeId) it.copy(commentsCount = newCount) else it
        }
        Log.d(TAG, "Updated all public recipes comments count")

        _followingRecipes.value = _followingRecipes.value.map {
            if (it.id == recipeId) it.copy(commentsCount = newCount) else it
        }
        Log.d(TAG, "Updated following recipes comments count")

        _trendingRecipes.value = _trendingRecipes.value.map {
            if (it.id == recipeId) it.copy(commentsCount = newCount) else it
        }
        Log.d(TAG, "Updated trending recipes comments count")

        _recommendedRecipes.value = _recommendedRecipes.value.map {
            if (it.id == recipeId) it.copy(commentsCount = newCount) else it
        }
        Log.d(TAG, "Updated recommended recipes comments count")

        // If you keep a derived saved list, recompute to keep it consistent
        Log.d(TAG, "Updating saved recipes after comments count change")
        updateSavedRecipes()
    }

    fun clearCommentError() {
        Log.d(TAG, "Clearing comment error")
        _commentError.value = null
    }

    fun loadComments(recipeId: String) {
        Log.d(TAG, "Loading comments for recipe: $recipeId")
        viewModelScope.launch {
            try {
                val token = sessionManager.getAccessToken()

                if (token == null) {
                    Log.e(TAG, "Cannot load comments - access token is null")
                    return@launch
                }

                Log.d(TAG, "Calling get comments use case")
                val comments = getCommentsUseCase(recipeId, token)
                Log.d(TAG, "Loaded ${comments.size} comments for recipe: $recipeId")
                _comments.value = comments
            } catch (e: Exception) {
                Log.e(TAG, "Error loading comments for recipe: $recipeId", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "RecipeViewModel cleared")
    }
}

enum class HomeFilter {
    ALL,
    FOLLOWING,
    TRENDING,
    SAVED
}