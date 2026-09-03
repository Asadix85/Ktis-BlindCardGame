package com.example.ktis.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetupGameScreen(
    onStartGame: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var playerCount by remember {
        mutableStateOf(2)
    }

    val names = remember {
        mutableStateListOf(
            "بازیکن ۱",
            "بازیکن ۲"
        )
    }

    fun updatePlayerCount(count: Int) {
        playerCount = count

        while (names.size < count) {
            names.add("بازیکن ${names.size + 1}")
        }

        while (names.size > count) {
            names.removeAt(names.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "تنظیم بازی",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "تعداد بازیکنان",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (playerCount > 2) {
                        updatePlayerCount(playerCount - 1)
                    }
                }
            ) {
                Text("−")
            }

            Text(
                text = playerCount.toString(),
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = {
                    if (playerCount < 8) {
                        updatePlayerCount(playerCount + 1)
                    }
                }
            ) {
                Text("+")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        names.forEachIndexed { index, name ->

            OutlinedTextField(
                value = name,
                onValueChange = {
                    names[index] = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                label = {
                    Text("بازیکن ${index + 1}")
                },
                singleLine = true
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                onStartGame(
                    names.map { it.trim() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("شروع بازی")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("بازگشت")
        }
    }
}