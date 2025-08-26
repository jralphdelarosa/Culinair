package com.example.culinair.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinair.presentation.theme.BrandBackground

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun CommunityScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground) // Light yellowish
            .padding(24.dp)
    ) {
        Text(
            text = "Community",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2F4F4F) // Dark green-gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Example placeholder content
        Text(
            text = "Community",
            color = Color(0xFF2F4F4F),
            fontSize = 16.sp
        )
    }
}