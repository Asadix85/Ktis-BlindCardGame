package com.example.ktis.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.theme.CarpetBurgundy
import com.example.ktis.ui.theme.CarpetBrown
import com.example.ktis.ui.theme.CarpetGold
import com.example.ktis.ui.theme.CarpetRed


/*
 * ============================================================
 * زمین بازی
 * ============================================================
 *
 * شامل حلقه‌ی چرخون میز + مرکز KTIS + کارت‌های روی میز.
 */
@Composable
fun TableArea(
    state: GameState,
    visibleCenterPile: List<PlayedCard>,
    animateCenterCards: Boolean,
    highlightedWinnerId: Int?,
    tableRotation: Float,
    modifier: Modifier = Modifier
) {

    val orderedPlayers = state.players.sortedBy { it.seat }
    val playerCount = orderedPlayers.size.coerceAtLeast(1)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        CarpetRed,
                        CarpetBurgundy
                    ),
                    radius = 1200f
                )
            )
            .border(
                2.dp,
                CarpetGold.copy(alpha = 0.7f),
                RoundedCornerShape(24.dp)
            )
            .border(
                6.dp,
                CarpetGold.copy(alpha = 0.15f),
                RoundedCornerShape(24.dp)
            )
    ) {

        val density = LocalDensity.current

        val boardRadiusPx =
            with(density) {
                (minOf(maxWidth, maxHeight) * GameConstants.BoardRadiusFactor).toPx()
            }

        val cardLandingRadiusPx =
            boardRadiusPx * GameConstants.CardLandingRadiusFactor

        val throwStartRadiusPx =
            boardRadiusPx * GameConstants.ThrowStartRadiusFactor

        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(tableRotation)
        ) {

            /*
             * مرکز میز
             */
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(60.dp)
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CarpetBurgundy,
                                CarpetBrown
                            )
                        ),
                        RoundedCornerShape(60.dp)
                    )
                    .border(
                        3.dp,
                        CarpetGold.copy(alpha = 0.55f),
                        RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "KTIS",
                    color = CarpetGold.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            /*
             * کارت‌ها
             */
            visibleCenterPile.forEachIndexed { index, playedCard ->

                val player =
                    orderedPlayers.firstOrNull {
                        it.id == playedCard.playerId
                    }

                if (player != null) {

                    val playerPosition =
                        orderedPlayers
                            .indexOfFirst {
                                it.id == player.id
                            }
                            .coerceAtLeast(0)

                    val isLatestCardOfPlayer =
                        visibleCenterPile
                            .indexOfLast {
                                it.playerId == playedCard.playerId
                            } == index

                    val isTiedCard =
                        state.tiedPlayerIds.contains(player.id) &&
                                isLatestCardOfPlayer

                    TableCard(
                        modifier = Modifier.align(Alignment.Center),
                        card = playedCard.card,
                        playerId = playedCard.playerId,
                        cardIndex = index,
                        playerPosition = playerPosition,
                        playerCount = playerCount,
                        cardLandingRadiusPx = cardLandingRadiusPx,
                        throwStartRadiusPx = throwStartRadiusPx,
                        isWinner = highlightedWinnerId == player.id,
                        isTied = isTiedCard,
                        animateDrop = animateCenterCards
                    )
                }
            }
        }
    }
}