package com.example.culinair.presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.culinair.presentation.components.CircularLogoWithLoadingRing

/**
* Created by John Ralph Dela Rosa on 8/9/2025.
*/
@Composable
fun LogoutDialog() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false) {}, // block clicks
        contentAlignment = Alignment.Center
    ) {
        CircularLogoWithLoadingRing()
    }
}