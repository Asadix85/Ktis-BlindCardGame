package com.example.ktis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val KtisColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = TableGreenLight,
    onSecondary = TextPrimary,
    background = TableGreen,
    onBackground = TextPrimary,
    surface = TableGreen,
    onSurface = TextPrimary,
    surfaceVariant = TableGreenLight,
    onSurfaceVariant = TextSecondary
)

private val KtisShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun KtisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KtisColorScheme,
        shapes = KtisShapes,
        content = content
    )
}