package com.example.culinair.presentation.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinair.presentation.theme.BrandBackgroundYellow
import com.example.culinair.presentation.theme.BrandGold

/**
 * Created by John Ralph Dela Rosa on 8/5/2025.
 */
@Composable
fun ErrorDialog(
    title: String = "Error",
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F4F4F)
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFF2F4F4F)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = BrandGold
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = if (onRetry != null) {
                {
                    TextButton(
                        onClick = {
                            onDismiss()
                            onRetry()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = BrandGold
                        )
                    ) {
                        Text("Retry")
                    }
                }
            } else null,
            containerColor = BrandBackgroundYellow,
            shape = RoundedCornerShape(16.dp)
        )
    }
}