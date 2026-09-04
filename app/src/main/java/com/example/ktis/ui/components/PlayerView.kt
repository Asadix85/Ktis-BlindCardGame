package com.example.ktis.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.Player
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight

@Composable
fun PlayerView(
    player: Player,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                Gold.copy(alpha = 0.20f)
            } else {
                TableGreenLight.copy(alpha = 0.75f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCurrent) "● ${player.name}" else player.name,
                fontSize = 14.sp,
                fontWeight = if (isCurrent) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                color = if (isCurrent) Gold else Color.White
            )

            Row(
                modifier = Modifier.padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🃏 ${player.remainingCards}",
                    fontSize = 12.sp
                )

                Text(
                    text = "🏆 ${player.score}",
                    fontSize = 12.sp
                )
            }
        }
    }
}