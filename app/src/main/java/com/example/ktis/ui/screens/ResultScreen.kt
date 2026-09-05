package com.example.ktis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.FinalResult

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)
private val WoodLight = Color(0xFF8A552F)
private val Gold = Color(0xFFFFD700)

@Composable
fun ResultScreen(
    result: FinalResult,
    playerNames: Map<Int, String>,
    onNewGame: () -> Unit,
    onMenu: () -> Unit
) {
    val sortedScores =
        result.scores.entries
            .sortedByDescending {
                it.value
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
                    Color.Black.copy(alpha = 0.18f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆",
                fontSize = 64.sp
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
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = result.winnerName,
                color = Gold,
                fontFamily = NazaninFont,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (result.isTieBroken) {
                Spacer(
                    modifier = Modifier.height(6.dp)
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

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "امتیاز نهایی",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                sortedScores.forEachIndexed {
                        index,
                        entry
                    ->
                    val name =
                        playerNames[entry.key]
                            ?: "بازیکن ${entry.key + 1}"

                    ScoreRow(
                        rank = index + 1,
                        name = name,
                        score = entry.value,
                        isWinner =
                            entry.key ==
                                    result.winnerId
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            WoodenResultButton(
                text = "🎴 بازی جدید",
                onClick = onNewGame
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WoodenResultButton(
                text = "منوی اصلی",
                onClick = onMenu
            )
        }
    }
}

@Composable
private fun ScoreRow(
    rank: Int,
    name: String,
    score: Int,
    isWinner: Boolean
) {
    val backgroundColor =
        if (isWinner) {
            Caramel.copy(alpha = 0.95f)
        } else {
            WoodMedium.copy(alpha = 0.94f)
        }

    val textColor =
        if (isWinner) {
            WoodDark
        } else {
            Caramel
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp
            )
            .background(
                backgroundColor,
                RoundedCornerShape(10.dp)
            )
            .then(
                if (isWinner) {
                    Modifier.background(
                        Caramel,
                        RoundedCornerShape(10.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = 16.dp,
                vertical = 13.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "$rank. $name",
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

            Text(
                text = "$score کارت",
                color = textColor,
                fontFamily = NazaninFont,
                fontSize = 19.sp,
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

@Composable
private fun WoodenResultButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val isPressed by
    interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed) {
                0.96f
            } else {
                1f
            },
        animationSpec =
            tween(80),
        label = "result_button_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .background(
                WoodMedium
            )
            .clickable(
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {
        Text(
            text = text,
            color = Caramel,
            fontFamily = NazaninFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}