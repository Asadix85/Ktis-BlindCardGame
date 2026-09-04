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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.TableGreenLight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

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

    /*
     * بازیکن فعلی همیشه باید پایین صفحه باشد.
     *
     * صندلی 7 = پایین
     */
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

    LaunchedEffect(currentSeat) {

        if (currentSeat != previousSeat) {

            val steps =
                (previousSeat - currentSeat + 8) % 8

            rotationTarget +=
                steps * 45f

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

        /*
         * =========================
         * اطلاعات بازی
         * =========================
         */

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

        /*
         * پیام
         */

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
                    text = message,

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

        /*
         * =========================
         * زمین بازی
         * =========================
         */

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

            /*
             * کل زمین می‌چرخد.
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(tableRotation)
            ) {

                /*
                 * =========================
                 * مرکز زمین
                 * =========================
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
                        text = "KTIS",

                        color =
                            Color.White.copy(
                                alpha = 0.65f
                            ),

                        fontWeight =
                            FontWeight.Bold,

                        fontSize = 18.sp
                    )
                }

                /*
                 * =========================
                 * کارت‌های بازی‌شده
                 * =========================
                 *
                 * هر کارت جلوی بازیکنی
                 * قرار می‌گیرد که آن را انداخته.
                 */

                visibleCenterPile.forEach { playedCard ->

                    val player =
                        state.players.firstOrNull {
                            it.id ==
                                    playedCard.playerId
                        }

                    if (player != null) {

                        CardAtSeat(
                            card =
                                playedCard.card,

                            seat =
                                player.seat,

                            isWinner =
                                highlightedWinnerId ==
                                        player.id,

                            animateThrow =
                                animateCenterCards
                        )
                    }
                }

                /*
                 * =========================
                 * فقط بازیکن فعلی
                 * =========================
                 *
                 * اسم بقیه نمایش داده نمی‌شود.
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
        }

        Spacer(
            Modifier.height(8.dp)
        )

        /*
         * =========================
         * نوبت فعلی
         * =========================
         */

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

        /*
         * =========================
         * دکمه‌ها
         * =========================
         */

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
 * =====================================================
 * کارت روی زمین، جلوی صندلی بازیکن
 * =====================================================
 */

@Composable
private fun CardAtSeat(
    card: com.example.ktis.domain.model.Card,
    seat: Int,
    isWinner: Boolean,
    animateThrow: Boolean
) {

    /*
     * جای کارت‌ها نسبت به مرکز زمین.
     *
     * صندلی 7 = پایین
     * صندلی 3 = بالا
     * صندلی 5 = راست
     * صندلی 1 = چپ
     */

    val alignment =
        when (seat) {

            7 ->
                Alignment.BottomCenter

            0 ->
                Alignment.BottomStart

            1 ->
                Alignment.CenterStart

            2 ->
                Alignment.TopStart

            3 ->
                Alignment.TopCenter

            4 ->
                Alignment.TopEnd

            5 ->
                Alignment.CenterEnd

            6 ->
                Alignment.BottomEnd

            else ->
                Alignment.Center
        }

    val horizontalPadding =
        when (seat) {

            0,
            2,
            4,
            6 -> 52.dp

            else -> 0.dp
        }

    val verticalPadding =
        when (seat) {

            0,
            2,
            4,
            6 -> 42.dp

            else -> 18.dp
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal =
                        horizontalPadding,

                    vertical =
                        verticalPadding
                ),
        contentAlignment =
            alignment
    ) {

        CardView(
            card = card,

            isWinner =
                isWinner,

            throwDirection =
                when (seat) {

                    7 -> 0f
                    3 -> 0f

                    0,
                    1,
                    2 -> -1f

                    4,
                    5,
                    6 -> 1f

                    else -> 0f
                },

            animateThrow =
                animateThrow,

            modifier =
                Modifier.width(72.dp)
        )
    }
}


/*
 * =====================================================
 * نشانگر بازیکن فعلی
 * =====================================================
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

                fontSize = 11.sp
            )
        }
    }
}