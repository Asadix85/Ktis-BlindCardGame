package com.example.ktis.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val KtisColorScheme = darkColorScheme(
    primary = Caramel,
    onPrimary = WoodDark,
    secondary = WoodMedium,
    onSecondary = Caramel,
    background = WoodDark,
    onBackground = Caramel,
    surface = WoodMedium,
    onSurface = Caramel,
    surfaceVariant = WoodLight,
    onSurfaceVariant = Caramel
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