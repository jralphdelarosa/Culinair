package com.example.culinair.presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.culinair.presentation.theme.BrandBackgroundYellow
import com.example.culinair.presentation.theme.BrandGold

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
@Composable
fun WelcomeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Thanks!", color = BrandGold)
            }
        },
        title = { Text("Email Confirmed!", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = { Text("Welcome to Culinair! Your email has been confirmed. Let’s get cooking!") },
        containerColor = BrandBackgroundYellow,
        titleContentColor = Color(0xFF2F4F4F),
        textContentColor = Color(0xFF2F4F4F)
    )
}