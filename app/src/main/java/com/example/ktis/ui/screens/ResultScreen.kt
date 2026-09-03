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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ktis.domain.model.FinalResult

@Composable
fun ResultScreen(
    result: FinalResult,
    onNewGame: () -> Unit,
    onMenu: () -> Unit
) {
    val sortedScores =
        result.scores.entries.sortedByDescending { it.value }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🏆",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "برنده بازی",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = result.winnerName,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (result.isTieBroken) {
            Text(
                text = "برنده با قرعه‌ی نهایی مشخص شد!",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = "امتیازها",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        sortedScores.forEachIndexed { index, entry ->

            val playerName =
                "بازیکن ${entry.key + 1}"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "${index + 1}. $playerName"
                )

                Text(
                    text = "${entry.value} کارت"
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onNewGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("بازی جدید")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = onMenu,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("منوی اصلی")
        }
    }
}