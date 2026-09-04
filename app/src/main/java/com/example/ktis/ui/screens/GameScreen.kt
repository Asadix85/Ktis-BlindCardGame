package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    canRequestCard: Boolean,
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

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "دور ${state.roundNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )

                Text(
                    text = currentPlayer.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TableGreenLight
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "وسط",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Text(
                        text = "${state.centerPile.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Players
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

        Spacer(Modifier.height(14.dp))

        // Center table
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(TableGreenLight)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(24.dp)
                ),
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

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "کارت وسط",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Message
        AnimatedVisibility(
            visible = message.isNotEmpty(),
            enter = fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBack
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    color = if (revealed) Gold else Color.White,
                    fontSize = 14.sp,
                    fontWeight = if (revealed) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Main actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDrawCard,
                enabled =
                    !revealed &&
                            currentPlayer.remainingCards > 0,
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "🃏 کارت بکش",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onRequestCard,
                enabled =
                    !revealed &&
                            canRequestCard,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "📨 درخواست",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("خروج از بازی")
        }
    }
}