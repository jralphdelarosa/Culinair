package com.example.culinair.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.culinair.R

/**
 * Created by John Ralph Dela Rosa on 8/22/2025.
 */

@Composable
fun LikeAnimation(
    triggerAnimation: Boolean,
    onAnimationEnd: () -> Unit
) {
    if (triggerAnimation) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart_animation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            speed = 0.6f, // 🔹 Slower for smooth effect
            restartOnPlay = false,
            clipSpec = LottieClipSpec.Progress(0f, 1f)
        )

        // ✅ Call onAnimationEnd when animation completes
        LaunchedEffect(progress) {
            if (progress >= 1f) {
                onAnimationEnd()
            }
        }

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(220.dp) //
        )
    }
}

@Composable
fun SaveAnimation(
    triggerAnimation: Boolean,
    onAnimationEnd: () -> Unit
) {
    if (triggerAnimation) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.save_animation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            speed = 0.6f, // 🔹 Slower for smooth effect
            restartOnPlay = false,
            clipSpec = LottieClipSpec.Progress(0f, 1f)
        )

        // ✅ Call onAnimationEnd when animation completes
        LaunchedEffect(progress) {
            if (progress >= 1f) {
                onAnimationEnd()
            }
        }

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(120.dp)
        )
    }
}