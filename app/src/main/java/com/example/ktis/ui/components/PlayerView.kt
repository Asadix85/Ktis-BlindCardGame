package com.example.ktis.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.Player
import com.example.ktis.ui.theme.Gold

@Composable
fun PlayerView(
    player: Player,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isCurrent)
                    Gold.copy(alpha = 0.18f)
                else
                    CardDefaults.cardColors().containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isCurrent) "● ${player.name}" else player.name,
                    fontSize = 17.sp
                )

                Text(
                    text = "امتیاز: ${player.score}",
                    fontSize = 13.sp
                )
            }

            Text(
                text = "🃏 ${player.remainingCards}",
                fontSize = 16.sp
            )
        }
    }
}