package com.example.culinair.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@Composable
fun CulinairButton(
    text: String,
    iconPainter: Painter? = null,
    iconImage: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(50)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2F4F4F),
            contentColor = Color.White
        ),
        enabled = enabled
    ) {
        if(iconPainter != null) {
            Icon(
                painter = iconPainter, // You'll need this icon
                contentDescription = "Google",
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
        if(iconImage != null){
            Icon(
                imageVector = iconImage,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text.uppercase(), fontWeight = FontWeight.Bold)
    }
}

