package com.example.ktis.ui.screens

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
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont


@Composable
fun MainMenuScreen(
    onStart: () -> Unit,
    onSettings: () -> Unit = {},
    onTutorial: () -> Unit = {}
) {

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
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "کارت بکش، شانس بیار!",
                color = Caramel.copy(alpha = 0.9f),
                fontFamily = NazaninFont,
                fontSize = 19.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                WoodenButton(
                    text = "شروع بازی",
                    onClick = onStart,
                    highlighted = true
                )

                WoodenButton(
                    text = "تنظیمات",
                    onClick = onSettings
                )

                WoodenButton(
                    text = "آموزش کامل",
                    onClick = onTutorial
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}