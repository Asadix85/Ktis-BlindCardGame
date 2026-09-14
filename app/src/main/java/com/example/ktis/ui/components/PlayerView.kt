package com.example.ktis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.Player
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium

@Composable
fun PlayerView(
    player: Player,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {

    val shape = RoundedCornerShape(14.dp)

    val gradient = if (isCurrent) {
        Brush.verticalGradient(
            colors = listOf(
                Caramel,
                WoodMedium
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                WoodMedium,
                WoodDark
            )
        )
    }

    val borderColor = if (isCurrent) {
        Caramel.copy(alpha = 0.75f)
    } else {
        Caramel.copy(alpha = 0.35f)
    }

    val textColor = if (isCurrent) {
        WoodDark
    } else {
        Caramel
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(gradient)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(
                horizontal = 10.dp,
                vertical = 9.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (isCurrent) {
                "● ${player.name}"
            } else {
                player.name
            },
            fontSize = 14.sp,
            fontFamily = NazaninFont,
            fontWeight = if (isCurrent) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = textColor
        )

        Row(
            modifier = Modifier.padding(top = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🃏 ${player.remainingCards}",
                fontSize = 12.sp,
                fontFamily = NazaninFont,
                color = textColor
            )

            Text(
                text = "🏆 ${player.score}",
                fontSize = 12.sp,
                fontFamily = NazaninFont,
                color = textColor
            )
        }
    }
}