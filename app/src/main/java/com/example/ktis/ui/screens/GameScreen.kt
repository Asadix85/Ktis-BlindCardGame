package com.example.ktis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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

    val playerCount =
        state.players.size

    val angleStep =
        360f / playerCount

    val currentSeat =
        state.currentPlayer.seat

    var rotationTarget by
    remember {
        mutableStateOf(0f)
    }

    var previousSeat by
    remember {
        mutableStateOf(currentSeat)
    }

    /*
     * نفر بعدی همیشه از سمت چپ می‌آید.
     */
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

            previousSeat =
                currentSeat
        }
    }

    val tableRotation by
    animateFloatAsState(
        targetValue =
            rotationTarget,

        animationSpec =
            tween(
                durationMillis = 700
            ),

        label =
            "table_rotation"
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .padding(12.dp)
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "دست ${state.roundNumber}",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "جمع‌شده: ${state.totalCollectedCards}",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        if (message.isNotEmpty()) {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (
                                highlightedWinnerId != null
                            ) {
                                Gold.copy(
                                    alpha = 0.18f
                                )
                            } else {
                                TableGreenLight
                            }
                    )
            ) {

                Text(
                    text =
                        message,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),

                    textAlign =
                        TextAlign.Center,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )
        }

        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Color(0xFF315C3A),
                        RoundedCornerShape(24.dp)
                    )
                    .border(
                        2.dp,
                        Color.White.copy(
                            alpha = 0.12f
                        ),
                        RoundedCornerShape(24.dp)
                    )
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(tableRotation)
            ) {

                /*
                 * مرکز زمین
                 */

                Box(
                    modifier =
                        Modifier
                            .size(120.dp)
                            .align(
                                Alignment.Center
                            )
                            .background(
                                Color(0xFF24482D),
                                RoundedCornerShape(60.dp)
                            )
                            .border(
                                2.dp,
                                Color.White.copy(
                                    alpha = 0.12f
                                ),
                                RoundedCornerShape(60.dp)
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "KTIS",

                        color =
                            Color.White.copy(
                                alpha = 0.65f
                            ),

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            18.sp
                    )
                }

                /*
                 * کارت‌های روی زمین
                 */

                visibleCenterPile.forEachIndexed {
                        index,
                        playedCard ->

                    val player =
                        state.players.firstOrNull {
                            it.id ==
                                    playedCard.playerId
                        }

                    if (player != null) {

                        /*
                         * فقط آخرین کارت هر بازیکن
                         * در tie زرد می‌شود.
                         */

                        val isLatestCardOfPlayer =
                            visibleCenterPile
                                .indexOfLast {
                                    it.playerId ==
                                            playedCard.playerId
                                } == index

                        val isTiedCard =
                            state.tiedPlayerIds.contains(
                                player.id
                            ) &&
                                    isLatestCardOfPlayer

                        CardAtSeat(
                            card =
                                playedCard.card,

                            seat =
                                player.seat,

                            playerCount =
                                playerCount,

                            cardIndex =
                                index,

                            isWinner =
                                highlightedWinnerId ==
                                        player.id,

                            isTied =
                                isTiedCard,

                            animateThrow =
                                animateCenterCards
                        )
                    }
                }
            }

            /*
             * نشانگر بازیکن فعلی
             */

            CurrentPlayerIndicator(
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(
                            bottom = 18.dp
                        ),

                playerName =
                    state.currentPlayer.name,

                remainingCards =
                    state.currentPlayer
                        .remainingCards
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            text =
                "نوبت: ${state.currentPlayer.name}",

            modifier =
                Modifier.fillMaxWidth(),

            textAlign =
                TextAlign.Center,

            fontWeight =
                FontWeight.Bold,

            fontSize =
                18.sp
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick =
                    onDrawCard,

                modifier =
                    Modifier.weight(1f),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Gold
                    )
            ) {

                Text(
                    text =
                        "🃏 انداختن کارت",

                    color =
                        Color.Black
                )
            }

            Button(
                onClick =
                    onShuffle,

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "🔀 بر زدن"
                )
            }
        }

        Spacer(
            Modifier.height(6.dp)
        )

        Button(
            onClick =
                onBack,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    "بازگشت به منوی اصلی"
            )
        }
    }
}


/*
 * =========================================================
 * کارت روی زمین
 * =========================================================
 *
 * 0 = پایین
 * 1 = چپ
 * 2 = بالا
 * 3 = راست
 *
 * کارت نیز همراه با همین زاویه قرار می‌گیرد،
 * بنابراین محور طولی کارت همیشه به سمت مرکز است.
 */

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

    val angle =
        Math.toRadians(
            seat *
                    (360.0 / playerCount)
        )

    val radius =
        0.29f

    /*
     * مختصات دایره:
     *
     * 0 = پایین
     * 1 = چپ
     * 2 = بالا
     * 3 = راست
     */

    val x =
        -sin(angle) * radius

    val y =
        cos(angle) * radius

    val stackOffset =
        (cardIndex % 5) * 7

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
        modifier =
            Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = xOffset,
                        y = yOffset
                    )
                },

        contentAlignment =
            Alignment.Center
    ) {

        CardView(
            card =
                card,

            isWinner =
                isWinner,

            isTied =
                isTied,

            /*
             * جهت قرارگیری خود کارت
             * و جهت پرتاب هر دو بر اساس
             * همان زاویه بازیکن هستند.
             */

            throwAngle =
                seat *
                        (360f / playerCount),

            animateThrow =
                animateThrow,

            modifier =
                Modifier.width(72.dp)
        )
    }
}


/*
 * =========================================================
 * نشانگر بازیکن فعلی
 * =========================================================
 */

@Composable
private fun CurrentPlayerIndicator(
    modifier: Modifier,
    playerName: String,
    remainingCards: Int
) {

    Card(
        modifier =
            modifier,

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Gold.copy(
                        alpha = 0.92f
                    )
            ),

        shape =
            RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 8.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "● $playerName",

                color =
                    Color.Black,

                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "$remainingCards کارت",

                color =
                    Color.Black.copy(
                        alpha = 0.75f
                    ),

                fontSize =
                    11.sp
            )
        }
    }
}