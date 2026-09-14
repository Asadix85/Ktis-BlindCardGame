package com.example.ktis.ui.screens.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.CarpetBrown
import com.example.ktis.ui.theme.CarpetGold
import com.example.ktis.ui.theme.Gold


@Composable
fun MessageCard(
    message: String,
    highlightedWinnerId: Int?,
    modifier: Modifier = Modifier
) {

    if (message.isEmpty()) return

    val winnerPulse by animateFloatAsState(
        targetValue =
            if (highlightedWinnerId != null) 1f else 0f,
        animationSpec = tween(
            durationMillis = 260,
            easing = FastOutSlowInEasing
        ),
        label = "winner_pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(1f + 0.015f * winnerPulse),
        colors = CardDefaults.cardColors(
            containerColor =
                if (highlightedWinnerId != null) {
                    Gold.copy(alpha = 0.94f)
                } else {
                    CarpetBrown.copy(alpha = 0.94f)
                }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            color =
                if (highlightedWinnerId != null) {
                    Color.Black
                } else {
                    CarpetGold
                },
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}