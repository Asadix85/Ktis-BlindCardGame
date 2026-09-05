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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ktis.domain.model.PlayerSetup

@Composable
fun SetupGameScreen(
    onStartGame: (List<PlayerSetup>) -> Unit,
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

    fun updatePlayerCount(
        count: Int
    ) {

        playerCount = count

        while (names.size < count) {
            names.add(
                "بازیکن ${names.size + 1}"
            )
        }

        while (names.size > count) {
            names.removeAt(
                names.lastIndex
            )
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "تنظیم بازی",
            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Spacer(
            Modifier.height(20.dp)
        )

        /*
         * =========================
         * تعداد بازیکنان
         * =========================
         */

        Text(
            text = "تعداد بازیکنان",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Button(
                onClick = {

                    if (playerCount > 2) {
                        updatePlayerCount(
                            playerCount - 1
                        )
                    }
                }
            ) {
                Text("−")
            }

            Text(
                text =
                    playerCount.toString(),

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium
            )

            Button(
                onClick = {

                    if (playerCount < 8) {
                        updatePlayerCount(
                            playerCount + 1
                        )
                    }
                }
            ) {
                Text("+")
            }
        }

        Spacer(
            Modifier.height(20.dp)
        )

        /*
         * =========================
         * نام بازیکنان
         * =========================
         */

        names.forEachIndexed { index, name ->

            OutlinedTextField(

                value = name,

                onValueChange = {
                    names[index] = it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 8.dp
                        ),

                label = {
                    Text(
                        "بازیکن ${index + 1}"
                    )
                },

                singleLine = true,

                isError =
                    name.trim().isEmpty()
            )
        }

        Spacer(
            Modifier.height(20.dp)
        )

        /*
         * =========================
         * توضیح جایگاه‌ها
         * =========================
         */

        Text(
            text =
                "جای بازیکن‌ها به‌صورت خودکار تنظیم می‌شود.",

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Spacer(
            Modifier.height(6.dp)
        )

        Text(
            text =
                "زاویه بین بازیکن‌ها = ۳۶۰ ÷ تعداد بازیکنان",

            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )

        Spacer(
            Modifier.height(24.dp)
        )

        /*
         * =========================
         * شروع بازی
         * =========================
         */

        Button(
            onClick = {

                val players =
                    names.map { name ->

                        PlayerSetup(
                            name = name.trim(),
                            seat = 0
                        )
                    }

                onStartGame(players)
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                names.all {
                    it.trim().isNotEmpty()
                }
        ) {

            Text("شروع بازی")
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Button(
            onClick = onBack,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("بازگشت")
        }
    }
}