package com.example.ktis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight

@Composable
fun ResultScreen(
    result: FinalResult,
    playerNames: Map<Int, String>,
    onNewGame: () -> Unit,
    onMenu: () -> Unit
) {
    val sortedScores = result.scores.entries
        .sortedByDescending { it.value }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🏆",
            fontSize = 82.sp
        )

        Text(
            text = "برنده بازی",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = result.winnerName,
            style = MaterialTheme.typography.displaySmall,
            color = Gold,
            fontWeight = FontWeight.Bold
        )

        if (result.isTieBroken) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "🎲 برنده با قرعه‌ی نهایی مشخص شد",
                color = Gold
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "امتیاز نهایی",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(10.dp))

        sortedScores.forEachIndexed { index, entry ->

            val name = playerNames[entry.key]
                ?: "بازیکن ${entry.key + 1}"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (entry.key == result.winnerId)
                            Gold.copy(alpha = 0.18f)
                        else
                            TableGreenLight
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. $name",
                        fontWeight =
                            if (entry.key == result.winnerId)
                                FontWeight.Bold
                            else
                                FontWeight.Normal
                    )

                    Text("${entry.value} کارت")
                }
            }
        }

        Spacer(
            Modifier
                .height(16.dp)
                .weight(1f)
        )

        Button(
            onClick = onNewGame,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold
            )
        ) {
            Text(
                "🎴 بازی جدید",
                color = androidx.compose.ui.graphics.Color.Black
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onMenu,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("منوی اصلی")
        }
    }
}