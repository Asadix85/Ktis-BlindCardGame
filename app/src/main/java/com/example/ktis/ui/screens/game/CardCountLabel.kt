package com.example.ktis.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CardCountLabel(
    count: Int,
    modifier: Modifier = Modifier
) {

    if (count <= 0) return

    Box(
        modifier = modifier
            .padding(bottom = 4.dp)
            .background(
                color = Color.Black.copy(alpha = 0.72f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}