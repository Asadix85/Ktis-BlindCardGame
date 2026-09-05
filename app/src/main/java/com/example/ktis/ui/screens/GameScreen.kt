package com.example.ktis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val CarpetRed = Color(0xFF6B2028)
private val CarpetBurgundy = Color(0xFF48151C)
private val CarpetBrown = Color(0xFF321A15)
private val CarpetGold = Color(0xFFC58A45)

@Composable
fun GameScreen(
    state: GameState,
    visibleCenterPile: List<PlayedCard>,
    animateCenterCards: Boolean,
    message: String,
    highlightedWinnerId: Int?,
    onDrawCard: () -> Unit,
    onShuffle: () -> Unit,
    onBack: () -> Unit
) {
    val playerCount = state.players.size
    val angleStep = 360f / playerCount
    val currentSeat = state.currentPlayer.seat

    var rotationTarget by remember {
        mutableStateOf(0f)
    }

    var previousSeat by remember {
        mutableStateOf(currentSeat)
    }

    LaunchedEffect(currentSeat) {
        if (currentSeat != previousSeat) {
            val seatDifference =
                (
                        currentSeat -
                                previousSeat +
                                playerCount
                        ) % playerCount

            rotationTarget -=
                seatDifference * angleStep

            previousSeat = currentSeat
        }
    }

    val tableRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = tween(1100),
        label = "table_rotation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.game_persian_carpet
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.22f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "دست ${state.roundNumber}",
                    color = CarpetGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "جمع‌شده: ${state.totalCollectedCards}",
                    color = CarpetGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (highlightedWinnerId != null) {
                                Gold.copy(alpha = 0.92f)
                            } else {
                                CarpetBrown.copy(alpha = 0.94f)
                            }
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        color =
                            if (highlightedWinnerId != null) {
                                Color.Black
                            } else {
                                CarpetGold
                            },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        CarpetRed,
                        RoundedCornerShape(24.dp)
                    )
                    .border(
                        3.dp,
                        CarpetGold.copy(alpha = 0.65f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(tableRotation)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .background(
                                CarpetBurgundy,
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

                    visibleCenterPile.forEachIndexed {
                            index,
                            playedCard
                        ->
                        val player =
                            state.players.firstOrNull {
                                it.id == playedCard.playerId
                            }

                        if (player != null) {
                            val isLatestCardOfPlayer =
                                visibleCenterPile.indexOfLast {
                                    it.playerId ==
                                            playedCard.playerId
                                } == index

                            val isTiedCard =
                                state.tiedPlayerIds.contains(
                                    player.id
                                ) &&
                                        isLatestCardOfPlayer

                            CardAtSeat(
                                card = playedCard.card,
                                seat = player.seat,
                                playerCount = playerCount,
                                cardIndex = index,
                                isWinner =
                                    highlightedWinnerId ==
                                            player.id,
                                isTied = isTiedCard,
                                animateThrow =
                                    animateCenterCards
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            PlayerCardStack(
                remainingCards =
                    state.currentPlayer.remainingCards
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "نوبت: ${state.currentPlayer.name}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = CarpetGold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDrawCard,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CarpetGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "🃏 انداختن کارت",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onShuffle,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CarpetBrown,
                        contentColor = CarpetGold
                    )
                ) {
                    Text(
                        text = "🔀 بر زدن",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CarpetBurgundy,
                    contentColor = CarpetGold
                )
            ) {
                Text(
                    text = "بازگشت به منوی اصلی",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlayerCardStack(
    remainingCards: Int
) {
    if (remainingCards <= 0) {
        Spacer(
            modifier = Modifier.height(54.dp)
        )
        return
    }

    val cardWidth = 46.dp
    val cardHeight = 66.dp
    val maxStackHeight = 68.dp

    val visibleCards =
        minOf(remainingCards, 18)

    val spacing =
        if (visibleCards <= 1) {
            0.dp
        } else {
            ((maxStackHeight.value - cardHeight.value) /
                    (visibleCards - 1)).dp
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxStackHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
        ) {
            repeat(visibleCards) { index ->
                Image(
                    painter = painterResource(
                        id = R.drawable.card_back
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .offset(
                            y = -(spacing * index)
                        )
                )
            }
        }

        CardCountLabel(
            count = remainingCards
        )
    }
}

@Composable
private fun CardCountLabel(
    count: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                CarpetBrown.copy(alpha = 0.92f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "$count کارت",
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 3.dp
            ),
            color = CarpetGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CardAtSeat(
    card: Card,
    seat: Int,
    playerCount: Int,
    cardIndex: Int,
    isWinner: Boolean,
    isTied: Boolean,
    animateThrow: Boolean
) {
    val angle = Math.toRadians(
        seat * (360.0 / playerCount)
    )

    val radius = 0.29f

    val x = -sin(angle) * radius
    val y = cos(angle) * radius

    val stackOffset = (cardIndex % 5) * 7

    val xOffset =
        (x * 1000).roundToInt() +
                if (cardIndex % 2 == 0) {
                    stackOffset
                } else {
                    -stackOffset
                }

    val yOffset =
        (y * 1000).roundToInt() +
                if (cardIndex % 2 == 0) {
                    -stackOffset
                } else {
                    stackOffset
                }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                IntOffset(
                    x = xOffset,
                    y = yOffset
                )
            },
        contentAlignment = Alignment.Center
    ) {
        CardView(
            card = card,
            isWinner = isWinner,
            isTied = isTied,
            throwAngle =
                seat * (360f / playerCount),
            animateThrow = animateThrow,
            modifier = Modifier.width(72.dp)
        )
    }
}