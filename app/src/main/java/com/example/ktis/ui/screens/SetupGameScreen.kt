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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.PlayerSetup

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)

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

    fun updatePlayerCount(count: Int) {
        playerCount = count

        while (names.size < count) {
            names.add(
                "بازیکن ${names.size + 1}"
            )
        }

        while (names.size > count) {
            names.removeAt(names.lastIndex)
        }
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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KTIS",
                color = Caramel,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "تنظیم بازی",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "تعداد بازیکنان",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WoodenSmallButton(
                    text = "−",
                    enabled = playerCount > 2,
                    onClick = {
                        if (playerCount > 2) {
                            updatePlayerCount(
                                playerCount - 1
                            )
                        }
                    }
                )

                Text(
                    text = playerCount.toString(),
                    color = Caramel,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                WoodenSmallButton(
                    text = "+",
                    enabled = playerCount < 8,
                    onClick = {
                        if (playerCount < 8) {
                            updatePlayerCount(
                                playerCount + 1
                            )
                        }
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "نام بازیکنان",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            names.forEachIndexed { index, name ->
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        names[index] = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 12.dp
                        ),
                    label = {
                        Text(
                            text = "بازیکن ${index + 1}",
                            fontFamily = NazaninFont
                        )
                    },
                    singleLine = true,
                    isError = name.trim().isEmpty()
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WoodenWideButton(
                text = "شروع بازی",
                enabled = names.all {
                    it.trim().isNotEmpty()
                },
                onClick = {
                    val players =
                        names.map { name ->
                            PlayerSetup(
                                name = name.trim(),
                                seat = 0
                            )
                        }

                    onStartGame(players)
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WoodenWideButton(
                text = "بازگشت",
                onClick = onBack
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun WoodenSmallButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) {
            0.92f
        } else {
            1f
        },
        animationSpec = tween(80),
        label = "small_button_press"
    )

    Box(
        modifier = Modifier
            .width(64.dp)
            .height(52.dp)
            .scale(scale)
            .background(
                if (enabled) {
                    WoodMedium
                } else {
                    WoodDark.copy(alpha = 0.55f)
                }
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) {
                Caramel
            } else {
                Caramel.copy(alpha = 0.3f)
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WoodenWideButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) {
            0.96f
        } else {
            1f
        },
        animationSpec = tween(80),
        label = "wide_button_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .background(
                if (enabled) {
                    WoodMedium
                } else {
                    WoodDark.copy(alpha = 0.55f)
                }
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) {
                Caramel
            } else {
                Caramel.copy(alpha = 0.35f)
            },
            fontFamily = NazaninFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}