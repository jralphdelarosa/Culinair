package com.example.culinair.presentation.screens.post_dish

import android.net.Uri
import android.util.Log
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

    companion object {
        private const val TAG = "CreateRecipeViewModel"
    }

    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        Log.d(TAG, "Updating title: $title")
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        Log.d(TAG, "Updating description: ${description.take(50)}...") // Only log first 50 chars
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun addIngredient() {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        currentIngredients.add("")
        Log.d(TAG, "Added ingredient. Total ingredients: ${currentIngredients.size}")
        _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
    }

    fun updateIngredient(index: Int, ingredient: String) {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        if (index < currentIngredients.size) {
            Log.d(TAG, "Updating ingredient at index $index: $ingredient")
            currentIngredients[index] = ingredient
            _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
        } else {
            Log.w(TAG, "Attempted to update ingredient at invalid index: $index")
        }
    }

    fun removeIngredient(index: Int) {
        val currentIngredients = _uiState.value.ingredients.toMutableList()
        if (index < currentIngredients.size) {
            val removedIngredient = currentIngredients[index]
            currentIngredients.removeAt(index)
            Log.d(
                TAG,
                "Removed ingredient at index $index: '$removedIngredient'. Remaining: ${currentIngredients.size}"
            )
            _uiState.value = _uiState.value.copy(ingredients = currentIngredients)
        } else {
            Log.w(TAG, "Attempted to remove ingredient at invalid index: $index")
        }
    }

    fun addStep() {
        val currentSteps = _uiState.value.steps.toMutableList()
        currentSteps.add("")
        Log.d(TAG, "Added step. Total steps: ${currentSteps.size}")
        _uiState.value = _uiState.value.copy(steps = currentSteps)
    }

    fun updateStep(index: Int, step: String) {
        val currentSteps = _uiState.value.steps.toMutableList()
        if (index < currentSteps.size) {
            Log.d(TAG, "Updating step at index $index: ${step.take(30)}...")
            currentSteps[index] = step
            _uiState.value = _uiState.value.copy(steps = currentSteps)
        } else {
            Log.w(TAG, "Attempted to update step at invalid index: $index")
        }
    }

    fun removeStep(index: Int) {
        val currentSteps = _uiState.value.steps.toMutableList()
        if (index < currentSteps.size) {
            currentSteps.removeAt(index)
            Log.d(TAG, "Removed step at index $index. Remaining steps: ${currentSteps.size}")
            _uiState.value = _uiState.value.copy(steps = currentSteps)
        } else {
            Log.w(TAG, "Attempted to remove step at invalid index: $index")
        }
    }

    fun updateImage(uri: Uri?) {
        Log.d(TAG, "Updating image URI: $uri")
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun updateCategory(category: String) {
        Log.d(TAG, "Updating category: $category")
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateCookTime(minutes: Int) {
        Log.d(TAG, "Updating cook time: $minutes minutes")
        _uiState.value = _uiState.value.copy(cookTimeMinutes = minutes)
    }

    fun updateDifficulty(difficulty: String) {
        Log.d(TAG, "Updating difficulty: $difficulty")
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun updateTagsInput(input: String) {
        Log.d(TAG, "Updating tags input: $input")
        _uiState.value = _uiState.value.copy(tagsInput = input)
    }

    fun processTagsInput() {
        val tagList = _uiState.value.tagsInput
            .split(Regex("[,\\s]+"))  // Split by one or more commas/spaces
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        Log.d(TAG, "Input: '${_uiState.value.tagsInput}'")
        Log.d(TAG, "Processed tags: ${tagList.joinToString(", ")}")
        _uiState.value = _uiState.value.copy(tags = tagList)
    }

    fun createRecipe(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "Starting recipe creation...")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val state = _uiState.value
            Log.d(
                TAG,
                "Recipe data - Title: '${state.title}', Ingredients: ${state.ingredients.size}, Steps: ${state.steps.size}"
            )

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
                    Log.d(TAG, "Recipe created successfully!")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "Unknown error"
                    Log.e(TAG, "Failed to create recipe: $errorMessage", exception)
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
        Log.d(TAG, "Clearing form - resetting all fields")
        _uiState.value = CreateRecipeUiState()
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
    val tagsInput: String = "",
    val cookTimeMinutes: Int = 30,
    val difficulty: String = "Easy",
    val isLoading: Boolean = false,
    val error: String? = null
)