package com.example.culinair.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.model.RecipePreviewUiModel
import com.example.culinair.domain.usecase.home.GetAllPublicRecipesUseCase
import com.example.culinair.domain.usecase.home.GetFollowingRecipesUseCase
import com.example.culinair.domain.usecase.home.GetRecommendedRecipesUseCase
import com.example.culinair.domain.usecase.home.GetTrendingRecipesUseCase
import com.example.culinair.domain.usecase.home.LikeRecipeUseCase
import com.example.culinair.domain.usecase.home.SaveRecipeUseCase
import com.example.culinair.domain.usecase.home.UnsaveRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/6/2025.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getAllPublicRecipesUseCase: GetAllPublicRecipesUseCase,
    private val getFollowingRecipesUseCase: GetFollowingRecipesUseCase,
    private val getTrendingRecipesUseCase: GetTrendingRecipesUseCase,
    private val getRecommendedRecipesUseCase: GetRecommendedRecipesUseCase,
    private val likeRecipeUseCase: LikeRecipeUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val unsaveRecipeUseCase: UnsaveRecipeUseCase
) : ViewModel() {

    private val _allPublicRecipes = MutableStateFlow<List<RecipePreviewUiModel>>(emptyList())
    val allPublicRecipes: StateFlow<List<RecipePreviewUiModel>> = _allPublicRecipes

    private val _followingRecipes = MutableStateFlow<List<RecipePreviewUiModel>>(emptyList())
    val followingRecipes: StateFlow<List<RecipePreviewUiModel>> = _followingRecipes

    private val _trendingRecipes = MutableStateFlow<List<RecipePreviewUiModel>>(emptyList())
    val trendingRecipes: StateFlow<List<RecipePreviewUiModel>> = _trendingRecipes

    private val _recommendedRecipes = MutableStateFlow<List<RecipePreviewUiModel>>(emptyList())
    val recommendedRecipes: StateFlow<List<RecipePreviewUiModel>> = _recommendedRecipes

    private val _selectedFilter = MutableStateFlow(HomeFilter.ALL)
    val selectedFilter: StateFlow<HomeFilter> = _selectedFilter

    val recipes: StateFlow<List<RecipePreviewUiModel>> =
        combine(
            selectedFilter,
            followingRecipes,
            trendingRecipes,
            recommendedRecipes,
            allPublicRecipes
        ) { filter, following, trending, recommended, allPublic  ->
            when (filter) {
                HomeFilter.ALL -> allPublic
                HomeFilter.FOLLOWING -> following
                HomeFilter.TRENDING -> trending
                HomeFilter.QUICK_MEALS -> recommended.filter { it.category.contains("quick", true) }
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

            _allPublicRecipes.value = getAllPublicRecipesUseCase(userId,token)
            _followingRecipes.value = getFollowingRecipesUseCase(userId, token)
            _trendingRecipes.value = getTrendingRecipesUseCase(userId,token)
            _recommendedRecipes.value = getRecommendedRecipesUseCase(userId,token)
        }
    }

    fun likeRecipe(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch

            val result = likeRecipeUseCase(recipeId, userId, token)
            result?.let { likeResponse ->
                // Optimistically update the UI
                updateRecipeLikeStatus(recipeId, likeResponse.liked, likeResponse.likes_count)
            }
        }
    }

    private fun updateRecipeLikeStatus(recipeId: String, isLiked: Boolean, newLikesCount: Int) {
        // Update all recipe lists
        _allPublicRecipes.value = _allPublicRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likes = newLikesCount)
            } else recipe
        }

        _followingRecipes.value = _followingRecipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isLikedByCurrentUser = isLiked, likes = newLikesCount)
            } else recipe
        }

        // Update other lists similarly...
    }

    fun saveRecipe(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch
            val success = saveRecipeUseCase(userId, recipeId, token)
            if (success) {
                loadHomeContent() // or update state manually
            }
        }
    }

    fun unsaveRecipe(recipeId: String) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken() ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch
            val success = unsaveRecipeUseCase(userId, recipeId, token)
            if (success) {
                loadHomeContent()
            }
        }
    }
}

enum class HomeFilter {
    ALL,
    FOLLOWING,
    TRENDING,
    QUICK_MEALS
}
