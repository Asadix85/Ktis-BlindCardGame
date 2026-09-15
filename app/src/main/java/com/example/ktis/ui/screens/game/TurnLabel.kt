package com.example.ktis.ui.screens.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.CarpetGold


/*
 * ============================================================
 * برچسب «نوبت چه کسیه»
 * ============================================================
 *
 * وقتی نوبت عوض می‌شه، اسم با یه انیمیشن نرم
 * از پایین میاد و اسم قبلی از بالا می‌ره.
 */
@Composable
fun TurnLabel(
    playerName: String,
    isAI: Boolean,
    modifier: Modifier = Modifier
) {

    val displayName =
        if (isAI) {
            "$playerName 🤖"
        } else {
            playerName
        }

    AnimatedContent(
        targetState = displayName,
        transitionSpec = {

            (
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ) +
                            slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 350,
                                    easing = FastOutSlowInEasing
                                )
                            )
                    ).togetherWith(
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 200
                        )
                    ) +
                            slideOutVertically(
                                targetOffsetY = { -it / 2 },
                                animationSpec = tween(
                                    durationMillis = 250,
                                    easing = FastOutSlowInEasing
                                )
                            )
                )
        },
        label = "turn_label_animation",
        modifier = modifier
    ) { animatedName ->

        Text(
            text = "نوبت: $animatedName",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = CarpetGold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}