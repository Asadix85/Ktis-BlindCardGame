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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)

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

                SettingsButton(
                    text = "درباره KTIS",
                    onClick = {
                        showAbout = true
                    }
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            SettingsButton(
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
                        fontFamily = NazaninFont
                    )
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(80),
        label = "settings_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .background(WoodMedium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
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

            Box(
                modifier = Modifier
                    .width(82.dp)
                    .height(38.dp)
                    .background(
                        if (enabled) {
                            Caramel
                        } else {
                            WoodDark
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (enabled) "روشن" else "خاموش",
                    color = if (enabled) {
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

@Composable
private fun SettingsButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(80),
        label = "settings_button_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .background(WoodMedium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
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