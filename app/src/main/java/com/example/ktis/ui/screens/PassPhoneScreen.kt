package com.example.ktis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold

@Composable
fun PassPhoneScreen(
    playerName: String,
    centerPile: List<PlayedCard>,
    onContinue: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(28.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        /*
         * Show cards already played in the round.
         *
         * These cards are face-up and do not animate,
         * because they are already sitting on the table.
         */
        if (centerPile.isNotEmpty()) {

            Text(
                text =
                    "کارت‌های روی زمین",

                style =
                    MaterialTheme.typography
                        .titleMedium,

                color =
                    Gold,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                Modifier.size(12.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                centerPile.forEach { playedCard ->

                    CardView(

                        card =
                            playedCard.card,

                        isWinner =
                            false,

                        throwDirection =
                            0f,

                        /*
                         * These cards are already on the table.
                         * Do NOT throw them onto the screen again.
                         */
                        animateThrow =
                            false,

                        modifier =
                            Modifier.size(
                                width = 78.dp,
                                height = 115.dp
                            )
                    )
                }
            }

            Spacer(
                Modifier.size(28.dp)
            )
        }

        /*
         * Phone icon
         */
        Text(
            text = "📱",
            fontSize = 80.sp
        )

        Spacer(
            Modifier.size(24.dp)
        )

        /*
         * Turn
         */
        Text(
            text =
                "نوبت توئه",

            style =
                MaterialTheme.typography
                    .headlineMedium,

            color =
                Gold
        )

        Spacer(
            Modifier.size(10.dp)
        )

        /*
         * Player name
         */
        Text(
            text =
                playerName,

            style =
                MaterialTheme.typography
                    .displaySmall,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            Modifier.size(16.dp)
        )

        /*
         * Pass phone instruction
         */
        Text(
            text =
                "گوشی را به $playerName بده",

            style =
                MaterialTheme.typography
                    .titleMedium,

            textAlign =
                TextAlign.Center
        )

        Text(
            text =
                "مطمئن شو بازیکن قبلی کارت خودش را ندیده است.",

            style =
                MaterialTheme.typography
                    .bodyMedium,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier.padding(
                    top = 8.dp
                )
        )

        Spacer(
            Modifier.size(32.dp)
        )

        /*
         * Continue
         */
        Button(

            onClick =
                onContinue,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Gold
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    "آماده‌ام — ادامه",

                color =
                    Color.Black
            )
        }
    }
}