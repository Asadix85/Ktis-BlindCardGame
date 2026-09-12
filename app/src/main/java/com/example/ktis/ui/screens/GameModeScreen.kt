package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import kotlinx.coroutines.delay

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)

@Composable
fun GameModeScreen(
    onLocalGame: () -> Unit,
    onDeviceGame: () -> Unit,
    onOnlineGame: () -> Unit,
    onBack: () -> Unit
) {
    var showContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(80)
        showContent = true
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
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "انتخاب حالت بازی",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "روش بازی خودت رو انتخاب کن",
                color = Caramel.copy(alpha = 0.82f),
                fontFamily = NazaninFont,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            AnimatedVisibility(
                visible = showContent,
                enter =
                    fadeIn(
                        tween(400)
                    ) +
                            slideInVertically(
                                initialOffsetY = { 35 },
                                animationSpec =
                                    tween(
                                        450,
                                        easing =
                                            FastOutSlowInEasing
                                    )
                            )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(15.dp)
                ) {
                    GameModeButton(
                        text = "🎴 بازی لوکال",
                        subtitle = "همه با یک گوشی بازی می‌کنید",
                        onClick = onLocalGame
                    )

                    GameModeButton(
                        text = "📱 بازی چنددستگاهی",
                        subtitle = "بلوتوث و هات‌اسپات",
                        enabled = false,
                        onClick = onDeviceGame
                    )

                    GameModeButton(
                        text = "🌐 بازی چنددستگاهی",
                        subtitle = "اینترنت و بازی از راه دور",
                        enabled = false,
                        onClick = onOnlineGame
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            AnimatedVisibility(
                visible = showContent,
                enter =
                    fadeIn(
                        tween(
                            400,
                            delayMillis = 350
                        )
                    )
            ) {
                WoodenBackButton(
                    text = "بازگشت",
                    onClick = onBack
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}

@Composable
private fun GameModeButton(
    text: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val isPressed by
    interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed && enabled) {
                0.96f
            } else {
                1f
            },
        animationSpec = tween(80),
        label = "game_mode_button_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color =
                    if (enabled) {
                        Caramel
                    } else {
                        Caramel.copy(alpha = 0.4f)
                    },
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                color =
                    if (enabled) {
                        Caramel.copy(alpha = 0.78f)
                    } else {
                        Caramel.copy(alpha = 0.28f)
                    },
                fontFamily = NazaninFont,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WoodenBackButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val isPressed by
    interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed) {
                0.96f
            } else {
                1f
            },
        animationSpec = tween(80),
        label = "back_button_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale)
            .background(WoodMedium)
            .clickable(
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Caramel,
            fontFamily = NazaninFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}