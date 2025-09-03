package com.example.culinair.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.culinair.R
import com.example.culinair.presentation.theme.BrandBackground

/**
 * Created by John Ralph Dela Rosa on 7/25/2025.
 */
@Composable
fun CircularLogoWithLoadingRing(
    ringColor: Color = Color(0xFFB88C00), // Gold color
    backgroundColor: Color = BrandBackground // Light yellowish background
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the rotation of the outer ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp) // Optional, for overall consistency
    ) {
        // Circular Logo
        Image(
            painter = painterResource(id = R.drawable.culinair_circular_logo),
            contentDescription = "Loading",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )

        // Loading Ring
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation)
        ) {
            drawArc(
                color = ringColor,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}