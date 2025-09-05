package com.example.culinair.presentation.screens.post_dish

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGold

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDishScreen(
    viewModel: CreateRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        // Header
        TopAppBar(
            modifier = Modifier.padding(end = 10.dp),
            title = {
                Text(
                    text = "Create Recipe",
                    color = Color(0xFF2F4F4F),
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                TextButton(
                    onClick = {
                        viewModel.processTagsInput()
                        viewModel.createRecipe(
                            onSuccess = {
                                showSuccessDialog = true
                                viewModel.clearForm()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = BrandGold
                        )
                    } else {
                        Text(
                            text = "Post",
                            color = BrandGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BrandBackground
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, BrandGold)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.selectedImageUri != null) {
                            AsyncImage(
                                model = uiState.selectedImageUri,
                                contentDescription = "Recipe Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Image",
                                    tint = BrandGold,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add Recipe Photo",
                                    color = Color(0xFF2F4F4F),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Title
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Recipe Title", color = Color(0xFF2F4F4F)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGold,
                        unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                        focusedTextColor = Color(0xFF2F4F4F),
                        unfocusedTextColor = Color(0xFF2F4F4F)
                    )
                )
            }

            // Description
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("Description", color = Color(0xFF2F4F4F)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGold,
                        unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                        focusedTextColor = Color(0xFF2F4F4F),
                        unfocusedTextColor = Color(0xFF2F4F4F)
                    )
                )
            }

            // Ingredients Section
            item {
                Text(
                    text = "Ingredients",
                    color = Color(0xFF2F4F4F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            itemsIndexed(uiState.ingredients) { index, ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingredient,
                        onValueChange = { viewModel.updateIngredient(index, it) },
                        label = { Text("Ingredient ${index + 1}", color = Color(0xFF2F4F4F)) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGold,
                            unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                            focusedTextColor = Color(0xFF2F4F4F),
                            unfocusedTextColor = Color(0xFF2F4F4F)
                        )
                    )

                    if (uiState.ingredients.size > 1) {
                        IconButton(
                            onClick = { viewModel.removeIngredient(index) }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Remove",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::addIngredient,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Ingredient")
                }
            }

            // Steps Section
            item {
                Text(
                    text = "Cooking Steps",
                    color = Color(0xFF2F4F4F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            itemsIndexed(uiState.steps) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    OutlinedTextField(
                        value = step,
                        onValueChange = { viewModel.updateStep(index, it) },
                        label = { Text("Step ${index + 1}", color = Color(0xFF2F4F4F)) },
                        modifier = Modifier.weight(1f),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGold,
                            unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                            focusedTextColor = Color(0xFF2F4F4F),
                            unfocusedTextColor = Color(0xFF2F4F4F)
                        )
                    )

                    if (uiState.steps.size > 1) {
                        IconButton(
                            onClick = { viewModel.removeStep(index) }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::addStep,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Step")
                }
            }

            // Category & Details
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    val categories =
                        listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Appetizer")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = uiState.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", color = Color(0xFF2F4F4F)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGold,
                                unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                                focusedTextColor = Color(0xFF2F4F4F),
                                unfocusedTextColor = Color(0xFF2F4F4F)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = Color(0xFF2F4F4F)) },
                                    onClick = {
                                        viewModel.updateCategory(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Cook Time
                    OutlinedTextField(
                        value = uiState.cookTimeMinutes.toString(),
                        onValueChange = {
                            it.toIntOrNull()?.let { minutes ->
                                viewModel.updateCookTime(minutes)
                            }
                        },
                        label = { Text("Minutes", color = Color(0xFF2F4F4F)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGold,
                            unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                            focusedTextColor = Color(0xFF2F4F4F),
                            unfocusedTextColor = Color(0xFF2F4F4F)
                        )
                    )
                }
            }

            // Difficulty
            item {
                var expanded by remember { mutableStateOf(false) }
                val difficulties = listOf("Easy", "Medium", "Hard")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = uiState.difficulty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty", color = Color(0xFF2F4F4F)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGold,
                            unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                            focusedTextColor = Color(0xFF2F4F4F),
                            unfocusedTextColor = Color(0xFF2F4F4F)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        difficulties.forEach { difficulty ->
                            DropdownMenuItem(
                                text = { Text(difficulty, color = Color(0xFF2F4F4F)) },
                                onClick = {
                                    viewModel.updateDifficulty(difficulty)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Tags
            item {
                OutlinedTextField(
                    value = uiState.tagsInput, // Use tagsInput instead of tags
                    onValueChange = viewModel::updateTagsInput,
                    label = { Text("Tags (comma separated)", color = Color(0xFF2F4F4F)) },
                    placeholder = {
                        Text(
                            "e.g., vegetarian, quick, healthy",
                            color = Color(0xFF2F4F4F).copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGold,
                        unfocusedBorderColor = Color(0xFF2F4F4F).copy(alpha = 0.3f),
                        focusedTextColor = Color(0xFF2F4F4F),
                        unfocusedTextColor = Color(0xFF2F4F4F)
                    )
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Text("🎉 Recipe Posted!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                text = {
                    Text("Your delicious dish is now live on the feed! 🍽️😋")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}