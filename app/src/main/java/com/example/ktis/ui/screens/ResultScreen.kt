package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.PlayerStats
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.Gold
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium
import kotlinx.coroutines.delay


@Composable
fun ResultScreen(
    result: FinalResult,
    playerNames: Map<Int, String>,
    playerIsAI: Map<Int, Boolean>,
    onNewGame: () -> Unit,
    onMenu: () -> Unit
) {

    var showContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(150)
        showContent = true
    }

    val sortedScores =
        result.scores.entries
            .sortedWith(
                compareByDescending<Map.Entry<Int, Float>> {
                    it.value
                }.thenBy {
                    it.key
                }
            )

    val totalCards =
        result.scores.values.sum()

    val winnerScore =
        result.scores[result.winnerId] ?: 0f

    val winnerPercentage =
        if (totalCards > 0f) {
            (winnerScore * 100f) / totalCards
        } else {
            0f
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.menu_wood_background
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.12f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 28.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = showContent,
                enter =
                    fadeIn(
                        animationSpec = tween(500)
                    ) +
                            slideInVertically(
                                initialOffsetY = { -40 },
                                animationSpec = tween(
                                    550,
                                    easing = FastOutSlowInEasing
                                )
                            )
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🏆",
                        fontSize = 58.sp
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "برنده بازی",
                        color = Caramel,
                        fontFamily = NazaninFont,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = result.winnerName,
                        color = Gold,
                        fontFamily = NazaninFont,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "${formatScore(winnerScore)} کارت  •  ${
                                String.format(
                                    "%.1f",
                                    winnerPercentage
                                )
                            }٪ از کل کارت‌ها",
                        color = Caramel,
                        fontFamily = NazaninFont,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (result.sharedCardsApplied) {

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "🤝 کارت‌های باقی‌مانده بین بازیکنان مساوی تقسیم شد",
                            color = Caramel,
                            fontFamily = NazaninFont,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (result.isTieBroken) {

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "🎲 برنده با قرعه‌ی نهایی مشخص شد",
                            color = Caramel,
                            fontFamily = NazaninFont,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "امتیاز نهایی",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "کل کارت‌های جمع‌شده: ${formatScore(totalCards)}",
                color = Caramel.copy(alpha = 0.82f),
                fontFamily = NazaninFont,
                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                sortedScores.forEachIndexed {
                        index,
                        entry
                    ->

                    val name =
                        playerNames[entry.key]
                            ?: "بازیکن ${entry.key + 1}"

                    val percentage =
                        if (totalCards > 0f) {
                            (entry.value * 100f) /
                                    totalCards
                        } else {
                            0f
                        }

                    val stats =
                        result.playerStats[entry.key]
                            ?: PlayerStats()

                    ScoreRow(
                        rank = index + 1,
                        name = name,
                        isAI =
                            playerIsAI[entry.key] ?: false,
                        score = entry.value,
                        percentage = percentage,
                        roundWins = stats.roundWins,
                        tieCount = stats.tieCount,
                        isWinner =
                            entry.key == result.winnerId,
                        visible = showContent,
                        animationDelay =
                            100 + (index * 80)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WoodenButton(
                text = "بازی جدید",
                onClick = onNewGame,
                highlighted = true
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WoodenButton(
                text = "منوی اصلی",
                onClick = onMenu
            )
        }
    }
}


/*
 * ============================================================
 * فرمت کردن امتیاز
 * ============================================================
 *
 * اگه عدد صحیح بود، فقط عدد صحیح رو نشون بده (مثلاً «۵»).
 * اگه اعشار داشت، با یه رقم اعشار نشون بده (مثلاً «۲.۵»).
 */
private fun formatScore(score: Float): String {

    return if (score % 1f == 0f) {
        score.toInt().toString()
    } else {
        String.format("%.1f", score)
    }
}


/*
 * ============================================================
 * ردیف امتیاز هر بازیکن
 * ============================================================
 */
@Composable
private fun ScoreRow(
    rank: Int,
    name: String,
    isAI: Boolean,
    score: Float,
    percentage: Float,
    roundWins: Int,
    tieCount: Int,
    isWinner: Boolean,
    visible: Boolean,
    animationDelay: Int
) {

    var showRow by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(visible) {

        if (visible) {
            delay(animationDelay.toLong())
            showRow = true
        }
    }

    AnimatedVisibility(
        visible = showRow,
        enter =
            fadeIn(
                animationSpec = tween(350)
            ) +
                    slideInVertically(
                        initialOffsetY = { 25 },
                        animationSpec = tween(
                            400,
                            easing = FastOutSlowInEasing
                        )
                    )
    ) {

        val backgroundColor =
            if (isWinner) {
                Caramel.copy(alpha = 0.96f)
            } else {
                WoodMedium.copy(alpha = 0.94f)
            }

        val textColor =
            if (isWinner) {
                WoodDark
            } else {
                Caramel
            }

        val shape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundColor)
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                when (rank) {
                                    1 -> "🥇"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "$rank."
                                },
                            color = textColor,
                            fontSize =
                                if (rank <= 3) {
                                    20.sp
                                } else {
                                    18.sp
                                },
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                if (isAI) {
                                    "$name 🤖"
                                } else {
                                    name
                                },
                            color = textColor,
                            fontFamily = NazaninFont,
                            fontSize = 20.sp,
                            fontWeight =
                                if (isWinner) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                        )
                    }

                    Text(
                        text = "${formatScore(score)} کارت",
                        color = textColor,
                        fontFamily = NazaninFont,
                        fontSize = 18.sp,
                        fontWeight =
                            if (isWinner) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                    )
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isWinner) {
                                    WoodDark.copy(alpha = 0.25f)
                                } else {
                                    WoodDark.copy(alpha = 0.45f)
                                }
                            )
                    ) {

                        val progress by
                        animateFloatAsState(
                            targetValue =
                                percentage / 100f,
                            animationSpec =
                                tween(
                                    durationMillis = 700,
                                    delayMillis = 100,
                                    easing =
                                        FastOutSlowInEasing
                                ),
                            label = "score_progress"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(7.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isWinner) {
                                        Gold
                                    } else {
                                        Caramel
                                    }
                                )
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text =
                            "${
                                String.format(
                                    "%.1f",
                                    percentage
                                )
                            }٪",
                        color = textColor,
                        fontFamily = NazaninFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceEvenly,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "🏆 $roundWins برد",
                        color = textColor,
                        fontFamily = NazaninFont,
                        fontSize = 15.sp,
                        fontWeight =
                            if (isWinner) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                    )

                    Text(
                        text = "🤝 $tieCount تساوی",
                        color = textColor,
                        fontFamily = NazaninFont,
                        fontSize = 15.sp,
                        fontWeight =
                            if (isWinner) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                    )
                }
            }
        }
    }
}