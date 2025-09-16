package com.example.culinair.data.remote.model.response

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
data class Recipe(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val imageUrl: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val cookTimeMinutes: Int = 0,
    val difficulty: String = "",
    val createdAt: String = ""
)