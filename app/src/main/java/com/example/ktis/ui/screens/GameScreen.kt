package com.example.ktis.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ktis.domain.model.GameState

@Composable
fun GameScreen(
    state: GameState,
    lastCardText: String?,
    message: String,
    onDrawCard: () -> Unit,
    onRequestCard: () -> Unit,
    onBack: () -> Unit
) {
    val currentPlayer = state.currentPlayer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "دور ${state.roundNumber}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "نوبت: ${currentPlayer.name}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = lastCardText ?: "🂠",
                    style = MaterialTheme.typography.displayMedium
                )

                Text(
                    text = if (lastCardText == null)
                        "کارت هنوز رو نشده"
                    else
                        "آخرین کارت"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "کارت‌های وسط: ${state.centerPile.size}"
        )

        Text(
            text = "کارت‌های ${currentPlayer.name}: ${currentPlayer.remainingCards}"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (message.isNotEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = onDrawCard,
            enabled = currentPlayer.remainingCards > 0
        ) {
            Text("کارت بکش")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = onRequestCard,
            enabled = true
        ) {
            Text("درخواست کارت")
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        state.players.forEach { player ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(player.name)

                Text(
                    "کارت: ${player.remainingCards} | امتیاز: ${player.score}"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("خروج")
        }
    }
}