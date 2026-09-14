package com.example.ktis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium


@Composable
fun SettingsScreen(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    vibrationEnabled: Boolean,
    onSoundChanged: (Boolean) -> Unit,
    onMusicChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onBack: () -> Unit
) {

    var showAbout by remember {
        mutableStateOf(false)
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

            Text(
                text = "تنظیمات",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                SettingsToggleRow(
                    title = "صدا",
                    enabled = soundEnabled,
                    onClick = {
                        onSoundChanged(!soundEnabled)
                    }
                )

                SettingsToggleRow(
                    title = "موسیقی",
                    enabled = musicEnabled,
                    onClick = {
                        onMusicChanged(!musicEnabled)
                    }
                )

                SettingsToggleRow(
                    title = "لرزش",
                    enabled = vibrationEnabled,
                    onClick = {
                        onVibrationChanged(!vibrationEnabled)
                    }
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                WoodenButton(
                    text = "درباره KTIS",
                    onClick = {
                        showAbout = true
                    }
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            WoodenButton(
                text = "بازگشت",
                onClick = onBack
            )
        }
    }

    if (showAbout) {

        AlertDialog(
            onDismissRequest = {
                showAbout = false
            },
            containerColor = WoodDark,
            titleContentColor = Caramel,
            textContentColor = Caramel,
            title = {
                Text(
                    text = "KTIS",
                    fontFamily = NazaninFont,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "بازی کارت KTIS\n\n" +
                            "یک بازی کارتی شانسی برای ۲ تا ۸ بازیکن.\n\n" +
                            "سازنده: ASADIX",
                    fontFamily = NazaninFont,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbout = false
                    }
                ) {
                    Text(
                        text = "باشه",
                        color = Caramel,
                        fontFamily = NazaninFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}


/*
 * ============================================================
 * ردیف تنظیمات با کلید روشن/خاموش
 * ============================================================
 *
 * این از همون استایل دکمه‌ی چوبی استفاده می‌کنه:
 *  - گوشه‌ی گرد 18dp
 *  - گرادینت عمودی WoodMedium → WoodDark
 *  - حاشیه‌ی نازک کاراملی
 *  - انیمیشن فشردن
 *
 * داخلش یه متن سمت چپ و یه کلید روشن/خاموش سمت راسته.
 */
@Composable
private fun SettingsToggleRow(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "settings_toggle_press"
    )

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WoodMedium,
                        WoodDark
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Caramel.copy(alpha = 0.35f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            /*
             * کلید روشن/خاموش.
             *
             * خود کلید هم گوشه‌ی گرد داره و داخلش
             * متن «روشن» یا «خاموش» می‌شه.
             */
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (enabled) {
                            Caramel
                        } else {
                            WoodDark
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = Caramel.copy(
                            alpha = if (enabled) 0.8f else 0.3f
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text =
                        if (enabled) "روشن" else "خاموش",
                    color =
                        if (enabled) {
                            WoodDark
                        } else {
                            Caramel.copy(alpha = 0.6f)
                        },
                    fontFamily = NazaninFont,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}