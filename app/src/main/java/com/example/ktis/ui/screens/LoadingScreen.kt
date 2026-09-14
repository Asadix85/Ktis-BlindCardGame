package com.example.ktis.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import kotlinx.coroutines.delay


@Composable
fun LoadingScreen() {

    /*
     * ============================================================
     * وضعیت‌های انیمیشن
     * ============================================================
     */

    var showLogo by remember {
        mutableStateOf(false)
    }

    var showBrandName by remember {
        mutableStateOf(false)
    }

    var showGameName by remember {
        mutableStateOf(false)
    }

    var showProgressBar by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(120)
        showLogo = true

        delay(500)
        showBrandName = true

        delay(400)
        showGameName = true

        delay(300)
        showProgressBar = true
    }

    /*
     * ============================================================
     * پالس آروم روی لوگو
     * ============================================================
     *
     * یه نفس‌کشیدن خیلی ملایم که لوگو رو زنده نگه می‌داره.
     */

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "logo_pulse"
        )

    val logoPulse by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1600,
                        easing = FastOutSlowInEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "logo_pulse_value"
    )

    /*
     * ============================================================
     * پر شدن نوار پیشرفت
     * ============================================================
     */

    val progress by
    animateFloatAsState(
        targetValue =
            if (showProgressBar) 1f else 0f,
        animationSpec =
            tween(
                durationMillis = 1400,
                easing = LinearEasing
            ),
        label = "loading_progress"
    )

    /*
     * ============================================================
     * تایپ‌شدن نام برند حرف به حرف
     * ============================================================
     */

    val brandText = "ASADIX"

    var visibleBrandChars by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(showBrandName) {

        if (!showBrandName) return@LaunchedEffect

        brandText.forEachIndexed { index, _ ->

            delay(70)
            visibleBrandChars = index + 1
        }
    }

    /*
     * ============================================================
     * پس‌زمینه‌ی مشکی با گرادینت خیلی ملایم
     * ============================================================
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color.Black
                    ),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            /*
             * ====================================================
             * لوگو با fade + scale
             * ====================================================
             */

            val logoAlpha by
            animateFloatAsState(
                targetValue =
                    if (showLogo) 1f else 0f,
                animationSpec =
                    tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    ),
                label = "logo_alpha"
            )

            val logoScale by
            animateFloatAsState(
                targetValue =
                    if (showLogo) 1f else 0.75f,
                animationSpec =
                    tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing
                    ),
                label = "logo_scale"
            )

            Image(
                painter = painterResource(
                    id = R.drawable.asadix_logo
                ),
                contentDescription = "ASADIX Logo",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(logoAlpha)
                    .scale(logoScale * logoPulse)
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            /*
             * ====================================================
             * نام برند (تایپ حرف به حرف)
             * ====================================================
             */

            Text(
                text =
                    brandText
                        .take(visibleBrandChars)
                        .padEnd(
                            brandText.length,
                            ' '
                        ),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            /*
             * ====================================================
             * نام بازی با fade + slide از پایین
             * ====================================================
             */

            val gameNameAlpha by
            animateFloatAsState(
                targetValue =
                    if (showGameName) 1f else 0f,
                animationSpec =
                    tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    ),
                label = "game_name_alpha"
            )

            Text(
                text = "KTIS",
                color = Caramel.copy(
                    alpha = 0.85f * gameNameAlpha
                ),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                fontFamily = NazaninFont,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(48.dp)
            )

            /*
             * ====================================================
             * نوار پیشرفت
             * ====================================================
             */

            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Color.White.copy(alpha = 0.12f)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Caramel.copy(alpha = 0.4f),
                                    Caramel,
                                    Caramel.copy(alpha = 0.4f)
                                )
                            )
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            /*
             * ====================================================
             * متن کوچیک «در حال بارگذاری...»
             * ====================================================
             */

            val loadingTextAlpha by
            animateFloatAsState(
                targetValue =
                    if (showProgressBar) 0.7f else 0f,
                animationSpec =
                    tween(
                        durationMillis = 400
                    ),
                label = "loading_text_alpha"
            )

            Text(
                text = "در حال بارگذاری...",
                color = Color.White.copy(
                    alpha = loadingTextAlpha
                ),
                fontSize = 13.sp,
                fontFamily = NazaninFont,
                letterSpacing = 1.sp
            )

            /*
             * ====================================================
             * نسخه
             * ====================================================
             */

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "v1.0",
                color = Color.White.copy(
                    alpha = 0.25f
                ),
                fontSize = 11.sp,
                fontFamily = NazaninFont,
                letterSpacing = 1.sp
            )
        }
    }
}