package com.example.culinair.presentation.screens.post_dish

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.domain.usecase.recipe.CreateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val createRecipeUseCase: CreateRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun addIngredient() {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        currentIngredients.add("")
        _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
    }

    fun updateIngredient(index: Int, ingredient: String) {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        if (index < currentIngredients.size) {
            currentIngredients[index] = ingredient
            _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
        }
    }

    fun removeIngredient(index: Int) {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        if (index < currentIngredients.size) {
            currentIngredients.removeAt(index)
            _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
        }
    }

    fun addStep() {
        val currentSteps = _uiState.value.steps.toMutableList()
        currentSteps.add("")
        _uiState.value = _uiState.value.copy(steps = currentSteps)
    }

    fun updateStep(index: Int, step: String) {
        val currentSteps = _uiState.value.steps.toMutableList()
        if (index < currentSteps.size) {
            currentSteps[index] = step
            _uiState.value = _uiState.value.copy(steps = currentSteps)
        }
    }

    fun removeStep(index: Int) {
        val currentSteps = _uiState.value.steps.toMutableList()
        if (index < currentSteps.size) {
            currentSteps.removeAt(index)
            _uiState.value = _uiState.value.copy(steps = currentSteps)
        }
    }

    fun updateImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateCookTime(minutes: Int) {
        _uiState.value = _uiState.value.copy(cookTimeMinutes = minutes)
    }

    fun updateDifficulty(difficulty: String) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun updateTags(tags: String) {
        val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        _uiState.value = _uiState.value.copy(tags = tagList)
    }

    fun createRecipe(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val state = _uiState.value
            val result = createRecipeUseCase(
                title = state.title,
                description = state.description,
                ingredients = state.ingredients,
                steps = state.steps,
                imageUri = state.selectedImageUri,
                category = state.category,
                tags = state.tags,
                cookTimeMinutes = state.cookTimeMinutes,
                difficulty = state.difficulty
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    onError(errorMessage)
                }
            )
        }
    }

    fun clearForm() {
        _uiState.value = CreateRecipeUiState() // Resets everything
    }
}

// UI STATE
data class CreateRecipeUiState(
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = listOf(""),
    val steps: List<String> = listOf(""),
    val selectedImageUri: Uri? = null,
    val category: String = "",
    val tags: List<String> = emptyList(),
    val cookTimeMinutes: Int = 30,
    val difficulty: String = "Easy",
    val isLoading: Boolean = false,
    val error: String? = null
)