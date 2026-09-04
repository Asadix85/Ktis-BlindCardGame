package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.Card as GameCard
import com.example.ktis.domain.model.GameState
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.components.PlayerView
import com.example.ktis.ui.theme.CardBack
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight

@Composable
fun GameScreen(
    state: GameState,
    lastCard: GameCard?,
    revealed: Boolean,
    message: String,
    onDrawCard: () -> Unit,
    onRequestCard: () -> Unit,
    onBack: () -> Unit
) {
    val currentPlayer = state.currentPlayer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "دور ${state.roundNumber}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "نوبت ${currentPlayer.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gold
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = TableGreenLight
                )
            ) {
                Text(
                    text = "وسط: ${state.centerPile.size}",
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            state.players.forEach { player ->
                PlayerView(
                    player = player,
                    isCurrent = player.id == currentPlayer.id,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(TableGreenLight),
            contentAlignment = Alignment.Center
        ) {
            if (lastCard != null) {
                CardView(
                    card = lastCard,
                    faceUp = revealed,
                    modifier = Modifier.width(150.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎴",
                        fontSize = 64.sp
                    )

                    Text(
                        text = "کارت وسط",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(
            visible = revealed && message.isNotEmpty(),
            enter = fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardBack
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center,
                    color = Gold
                )
            }
        }

        if (!revealed && message.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardBack
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDrawCard,
                enabled = currentPlayer.remainingCards > 0,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold
                )
            ) {
                Text(
                    text = "🃏 کارت بکش",
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }

            OutlinedButton(
                onClick = onRequestCard,
                modifier = Modifier.weight(1f)
            ) {
                Text("📨 درخواست کارت")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("خروج از بازی")
        }
    }
}