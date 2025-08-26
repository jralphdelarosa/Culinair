package com.example.culinair.presentation.viewmodel.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.model.CommentUiModel
import com.example.culinair.domain.model.RecipeDetailUiModel
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
    private val getCommentsUseCase: GetCommentsUseCase
) : ViewModel() {

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
            when (filter) {
                HomeFilter.ALL -> allPublic
                HomeFilter.FOLLOWING -> following
                HomeFilter.TRENDING -> trending
                HomeFilter.SAVED -> saved
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadHomeContent()
    }

    fun selectFilter(filter: HomeFilter) {
        _selectedFilter.value = filter
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch

            _allPublicRecipes.value = getAllPublicRecipesUseCase(userId, token)
            _followingRecipes.value = getFollowingRecipesUseCase(userId, token)
            _trendingRecipes.value = getTrendingRecipesUseCase(userId, token)
            _recommendedRecipes.value = getRecommendedRecipesUseCase(userId, token)

            updateSavedRecipes()
        }
    }

    private fun updateSavedRecipes(){
        val savedRecipes = _allPublicRecipes.value.filter { recipe ->
            recipe.isSavedByCurrentUser
        }

        _savedRecipes.value = savedRecipes
    }

    fun likeRecipe(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch

            val result = likeRecipeUseCase(recipeId, userId, token)
            result?.let { likeResponse ->
                // Optimistically update the UI
                updateRecipeLikeStatus(recipeId, likeResponse.liked, likeResponse.likesCount)
            }
        }
    }

    private fun updateRecipeLikeStatus(recipeId: String, isLiked: Boolean, newLikesCount: Int) {
        // Update all recipe lists
        _allPublicRecipes.value = _allPublicRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe
        }

        _followingRecipes.value = _followingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe
        }

        _trendingRecipes.value = _trendingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likesCount = newLikesCount)
            } else recipe

        }
    }

    private fun updateRecipeSaveStatus(recipeId: String, isSaved: Boolean, newSavesCount: Int) {
        // Update all recipe lists
        _allPublicRecipes.value = _allPublicRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe
        }

        _followingRecipes.value = _followingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe
        }

        _trendingRecipes.value = _trendingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isSavedByCurrentUser = isSaved, savesCount = newSavesCount)
            } else recipe

        }
    }

    fun saveRecipe(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch

            val result = saveRecipeUseCase(recipeId, userId, token)
            result?.let { saveResponse ->
                // Optimistically update the UI
                updateRecipeSaveStatus(recipeId, saveResponse.saved, saveResponse.savesCount)
            }
        }
    }

    fun addComment(recipeId: String, parentCommentId: String? = null, content: String) {
        viewModelScope.launch {
            _isAddingComment.value = true
            _commentError.value = null

            try {
                val token = sessionManager.getAccessToken() ?: return@launch

                // Call addComment - it returns null on success (empty response)
                addCommentUseCase(token, recipeId, content, parentCommentId)

                // If we reach here without exception, it was successful
                // Always refresh the comments list
                loadComments(recipeId)

            } catch (e: Exception) {
                _commentError.value = "Error posting comment: ${e.message}"
                Log.e("RecipeViewModel", "Error adding comment", e)
            } finally {
                _isAddingComment.value = false
            }
        }
    }


    fun clearCommentError() {
        _commentError.value = null
    }

    fun loadComments(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            _comments.value = getCommentsUseCase(recipeId, token)
        }
    }
}

enum class HomeFilter {
    ALL,
    FOLLOWING,
    TRENDING,
    SAVED
}
