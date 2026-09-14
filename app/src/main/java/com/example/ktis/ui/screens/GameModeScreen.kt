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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.components.WoodenButtonWithSubtitle
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import kotlinx.coroutines.delay


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
                enter = fadeIn(tween(400)) +
                        slideInVertically(
                            initialOffsetY = { 35 },
                            animationSpec = tween(
                                450,
                                easing = FastOutSlowInEasing
                            )
                        )
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    WoodenButtonWithSubtitle(
                        text = "🎴 بازی لوکال",
                        subtitle = "همه با یک گوشی بازی می‌کنید",
                        highlighted = true,
                        onClick = onLocalGame
                    )

                    WoodenButtonWithSubtitle(
                        text = "📱 بازی چنددستگاهی",
                        subtitle = "بلوتوث و هات‌اسپات",
                        enabled = false,
                        badge = "به‌زودی",
                        onClick = onDeviceGame
                    )

                    WoodenButtonWithSubtitle(
                        text = "🌐 بازی چنددستگاهی",
                        subtitle = "اینترنت و بازی از راه دور",
                        enabled = false,
                        badge = "به‌زودی",
                        onClick = onOnlineGame
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    tween(
                        400,
                        delayMillis = 350
                    )
                )
            ) {

                WoodenButton(
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