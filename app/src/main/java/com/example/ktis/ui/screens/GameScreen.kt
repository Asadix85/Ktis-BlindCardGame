package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.components.PlayerView
import com.example.ktis.ui.theme.CardBack
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight

@Composable
fun GameScreen(
    state: GameState,
    visibleCenterPile: List<PlayedCard>,
    animateCenterCards: Boolean,
    message: String,
    canRequestCard: Boolean,
    highlightedWinnerId: Int?,
    onDrawCard: () -> Unit,
    onRequestCard: () -> Unit,
    onBack: () -> Unit
) {

    val currentPlayer =
        state.currentPlayer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(12.dp)
    ) {

        /*
         * Header
         */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text =
                        "دور ${state.roundNumber}",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    color =
                        Color.LightGray
                )

                Text(
                    text =
                        currentPlayer.name,
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    color =
                        Gold,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Card(
                shape =
                    RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            TableGreenLight
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "وسط",
                        fontSize = 11.sp,
                        color =
                            Color.LightGray
                    )

                    Text(
                        text =
                            "${visibleCenterPile.size}",
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Gold
                    )
                }
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        /*
         * Players
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            state.players.forEach { player ->

                PlayerView(
                    player = player,

                    isCurrent =
                        player.id ==
                                currentPlayer.id,

                    modifier =
                        Modifier.weight(1f)
                )
            }
        }

        Spacer(
            Modifier.height(14.dp)
        )

        /*
         * TABLE
         *
         * When there are no cards:
         * absolutely nothing is shown here.
         *
         * No 🎴
         * No "کارت‌ها روی زمین"
         */
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    TableGreenLight,
                    RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color =
                        Color.White.copy(
                            alpha = 0.08f
                        ),
                    shape =
                        RoundedCornerShape(24.dp)
                )
                .padding(10.dp),

            contentAlignment =
                Alignment.Center
        ) {

            if (visibleCenterPile.isNotEmpty()) {

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

                    visibleCenterPile.forEach { playedCard ->

                        val direction =
                            when (
                                playedCard.playerId % 3
                            ) {
                                0 -> -1f
                                1 -> 1f
                                else -> 0f
                            }

                        CardView(

                            card =
                                playedCard.card,

                            isWinner =
                                playedCard.playerId ==
                                        highlightedWinnerId,

                            throwDirection =
                                direction,

                            animateThrow =
                                animateCenterCards,

                            modifier =
                                Modifier.width(78.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            Modifier.height(10.dp)
        )

        /*
         * Message
         */
        AnimatedVisibility(
            visible =
                message.isNotEmpty(),
            enter =
                fadeIn()
        ) {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CardBack
                    )
            ) {

                Text(
                    text = message,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                    textAlign =
                        TextAlign.Center,

                    color =
                        if (
                            highlightedWinnerId != null
                        ) {
                            Color(0xFF20E070)
                        } else {
                            Color.White
                        },

                    fontSize =
                        14.sp,

                    fontWeight =
                        if (
                            highlightedWinnerId != null
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                )
            }
        }

        Spacer(
            Modifier.height(10.dp)
        )

        /*
         * Game buttons
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick =
                    onDrawCard,

                enabled =
                    currentPlayer.remainingCards > 0 &&
                            highlightedWinnerId == null,

                modifier =
                    Modifier
                        .weight(1.2f)
                        .height(52.dp),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Gold,
                        contentColor =
                            Color.Black
                    )
            ) {

                Text(
                    text =
                        "🃏 کارت بکش",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            OutlinedButton(

                onClick =
                    onRequestCard,

                enabled =
                    canRequestCard &&
                            highlightedWinnerId == null,

                modifier =
                    Modifier
                        .weight(1f)
                        .height(52.dp),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Text(
                    text =
                        "📨 درخواست",

                    fontSize =
                        13.sp
                )
            }
        }

        Spacer(
            Modifier.height(8.dp)
        )

        OutlinedButton(

            onClick =
                onBack,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(44.dp),

            shape =
                RoundedCornerShape(12.dp)
        ) {

            Text(
                "خروج از بازی"
            )
        }
    }
}