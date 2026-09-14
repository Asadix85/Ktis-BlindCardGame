package com.example.ktis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.components.WoodenButtonWithSubtitle
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import kotlinx.coroutines.delay


@Composable
fun LocalGameMenuScreen(
    continueEnabled: Boolean,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {

    var showOptions by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(100)
        showOptions = true
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
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
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

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "بازی لوکال",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "همه با یک گوشی بازی می‌کنید",
                color = Caramel.copy(alpha = 0.82f),
                fontFamily = NazaninFont,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                AnimatedVisibility(
                    visible = showOptions,
                    enter = fadeIn(
                        animationSpec = tween(400)
                    ) + slideInVertically(
                        initialOffsetY = { 60 },
                        animationSpec = tween(
                            500,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {

                    WoodenButtonWithSubtitle(
                        text = "🎴 بازی جدید",
                        subtitle = "شروع یک بازی تازه",
                        highlighted = true,
                        onClick = onNewGame
                    )
                }

                AnimatedVisibility(
                    visible = showOptions,
                    enter = fadeIn(
                        animationSpec = tween(
                            400,
                            delayMillis = 150
                        )
                    ) + slideInVertically(
                        initialOffsetY = { 60 },
                        animationSpec = tween(
                            500,
                            delayMillis = 150,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {

                    WoodenButtonWithSubtitle(
                        text = "💾 ادامه بازی قبلی",
                        subtitle = if (continueEnabled) {
                            "ادامه بازی ذخیره‌شده"
                        } else {
                            "بازی ذخیره‌شده‌ای وجود ندارد"
                        },
                        enabled = continueEnabled,
                        badge = if (!continueEnabled) {
                            "خالی"
                        } else {
                            null
                        },
                        onClick = onContinue
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            AnimatedVisibility(
                visible = showOptions,
                enter = fadeIn(
                    animationSpec = tween(
                        400,
                        delayMillis = 300
                    )
                )
            ) {

                WoodenButton(
                    text = "بازگشت",
                    onClick = onBack
                )
            }
        }
    }
}