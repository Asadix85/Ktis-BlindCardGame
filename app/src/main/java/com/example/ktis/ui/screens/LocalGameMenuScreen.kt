package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
private val ButtonShape = RoundedCornerShape(18.dp)

@Composable
fun LocalGameMenuScreen(
    continueEnabled: Boolean,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showOptions = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_wood_background),
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
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KTIS",
                style = TextStyle(
                    color = Caramel,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(0f, 4f),
                        blurRadius = 12f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "بازی لوکال",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "همه با یک گوشی بازی می‌کنید",
                color = Caramel.copy(alpha = 0.82f),
                fontFamily = NazaninFont,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                AnimatedVisibility(
                    visible = showOptions,
                    enter = fadeIn(animationSpec = tween(400)) +
                            slideInVertically(
                                initialOffsetY = { 60 },
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            )
                ) {
                    LocalGameButton(
                        text = "🎴 بازی جدید",
                        subtitle = "شروع یک بازی تازه",
                        onClick = onNewGame
                    )
                }

                AnimatedVisibility(
                    visible = showOptions,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 150)) +
                            slideInVertically(
                                initialOffsetY = { 60 },
                                animationSpec = tween(500, delayMillis = 150, easing = FastOutSlowInEasing)
                            )
                ) {
                    LocalGameButton(
                        text = "💾 ادامه بازی قبلی",
                        subtitle = if (continueEnabled) {
                            "ادامه بازی ذخیره‌شده"
                        } else {
                            "بازی ذخیره‌شده‌ای وجود ندارد"
                        },
                        enabled = continueEnabled,
                        onClick = onContinue
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = showOptions,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 300))
            ) {
                LocalGameButton(
                    text = "بازگشت",
                    subtitle = "",
                    onClick = onBack,
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun LocalGameButton(
    text: String,
    subtitle: String,
    enabled: Boolean = true,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = tween(100),
        label = "local_game_button_press"
    )

    val gradient = if (enabled) {
        Brush.verticalGradient(listOf(WoodMedium, WoodDark))
    } else {
        Brush.verticalGradient(
            listOf(WoodDark.copy(alpha = 0.45f), WoodDark.copy(alpha = 0.55f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 54.dp else 68.dp)
                .shadow(elevation = if (enabled) 6.dp else 0.dp, shape = ButtonShape)
                .clip(ButtonShape)
                .background(gradient)
                .border(
                    width = 1.dp,
                    color = Caramel.copy(alpha = if (enabled) 0.35f else 0.1f),
                    shape = ButtonShape
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = ripple(color = Caramel),
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = if (compact) 8.dp else 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    color = if (enabled) Caramel else Caramel.copy(alpha = 0.35f),
                    fontFamily = NazaninFont,
                    fontSize = if (compact) 20.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = subtitle,
                        color = if (enabled) Caramel.copy(alpha = 0.78f) else Caramel.copy(alpha = 0.28f),
                        fontFamily = NazaninFont,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!enabled && !compact) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Caramel)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "خالی",
                    color = WoodDark,
                    fontFamily = NazaninFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}