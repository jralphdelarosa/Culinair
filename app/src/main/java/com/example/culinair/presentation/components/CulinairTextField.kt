package com.example.culinair.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinair.presentation.theme.BrandGold

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@Composable
fun CulinairTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false
) {
    val visual = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    val keyboard = if (isPassword) KeyboardType.Password else KeyboardType.Email

    val textSize = 16.sp
    val labelSize = 15.sp
    val fieldHeight = 64.dp
    val iconSize = 24.dp

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(fieldHeight)
            .clip(RoundedCornerShape(4.dp)),
        textStyle = TextStyle(fontSize = textSize),
        visualTransformation = visual,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        label = {
            Text(text = label, fontSize = labelSize)
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = BrandGold,
            unfocusedIndicatorColor = Color(0xFF2F4F4F)
        )
    )
}