package com.example.ktis.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium


/*
 * دکمه‌ی چوبی مشترک.
 *
 * این همون استایل دکمه‌ی منوی اصلیه:
 *  - گوشه‌ی گرد 18dp
 *  - گرادینت عمودی WoodMedium → WoodDark
 *  - حاشیه‌ی نازک کاراملی
 *  - سایه
 *  - انیمیشن فشردن
 *
 * پارامتر highlighted برای دکمه‌های مهم‌تر
 * (مثل «آماده‌ام — ادامه» توی PassPhoneScreen)
 * که گرادینت روشن‌تری می‌گیره.
 */
@Composable
fun WoodenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    height: Int = 60
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed && enabled) 0.96f else 1f,
        animationSpec = tween(100),
        label = "wooden_button_press"
    )

    val shape = RoundedCornerShape(18.dp)

    val gradient = when {
        !enabled -> Brush.verticalGradient(
            listOf(
                WoodDark.copy(alpha = 0.45f),
                WoodDark.copy(alpha = 0.55f)
            )
        )

        highlighted -> Brush.verticalGradient(
            listOf(Caramel, WoodMedium)
        )

        else -> Brush.verticalGradient(
            listOf(WoodMedium, WoodDark)
        )
    }

    val borderColor = when {
        !enabled -> Caramel.copy(alpha = 0.1f)
        highlighted -> Caramel.copy(alpha = 0.65f)
        else -> Caramel.copy(alpha = 0.35f)
    }

    val textColor = when {
        !enabled -> Caramel.copy(alpha = 0.35f)
        highlighted -> WoodDark
        else -> Caramel
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = shape
            )
            .clip(shape)
            .background(gradient)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Caramel),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = NazaninFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


/*
 * نسخه‌ی دکمه‌ی چوبی با زیرنویس.
 *
 * برای دکمه‌هایی که یه متن اصلی + یه متن کوچیک‌تر زیرش دارن
 * (مثل «بازی جدید» + «شروع یک بازی تازه»).
 *
 * پارامتر compact ارتفاع رو کمتر می‌کنه (برای دکمه‌ی «بازگشت»).
 * پارامتر badge برای یه برچسب کوچیک گوشه‌ی دکمه (مثل «خالی» یا «به‌زودی»).
 */
@Composable
fun WoodenButtonWithSubtitle(
    text: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    compact: Boolean = false,
    badge: String? = null
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed && enabled) 0.96f else 1f,
        animationSpec = tween(100),
        label = "wooden_button_subtitle_press"
    )

    val shape = RoundedCornerShape(18.dp)

    val gradient = when {
        !enabled -> Brush.verticalGradient(
            listOf(
                WoodDark.copy(alpha = 0.45f),
                WoodDark.copy(alpha = 0.55f)
            )
        )

        highlighted -> Brush.verticalGradient(
            listOf(Caramel, WoodMedium)
        )

        else -> Brush.verticalGradient(
            listOf(WoodMedium, WoodDark)
        )
    }

    val borderColor = when {
        !enabled -> Caramel.copy(alpha = 0.1f)
        highlighted -> Caramel.copy(alpha = 0.65f)
        else -> Caramel.copy(alpha = 0.35f)
    }

    val textColor = when {
        !enabled -> Caramel.copy(alpha = 0.35f)
        highlighted -> WoodDark
        else -> Caramel
    }

    val subtitleColor = when {
        !enabled -> Caramel.copy(alpha = 0.28f)
        highlighted -> WoodDark.copy(alpha = 0.75f)
        else -> Caramel.copy(alpha = 0.78f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 54.dp else 68.dp)
                .shadow(
                    elevation = if (enabled) 6.dp else 0.dp,
                    shape = shape
                )
                .clip(shape)
                .background(gradient)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = shape
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = ripple(color = Caramel),
                    onClick = onClick
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = if (compact) 8.dp else 10.dp
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = text,
                    color = textColor,
                    fontFamily = NazaninFont,
                    fontSize = if (compact) 20.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (subtitle.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(1.dp)
                    )

                    Text(
                        text = subtitle,
                        color = subtitleColor,
                        fontFamily = NazaninFont,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (badge != null && !compact) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Caramel)
                    .padding(
                        horizontal = 10.dp,
                        vertical = 4.dp
                    )
            ) {

                Text(
                    text = badge,
                    color = WoodDark,
                    fontFamily = NazaninFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}