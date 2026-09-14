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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)
private val ButtonShape = RoundedCornerShape(18.dp)

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
            painter = painterResource(id = R.drawable.menu_wood_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // گرادینت overlay به‌جای آلفای ثابت - عمق بیشتر، تیره‌تر در بالا/پایین
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "کارت بکش، شانس بیار!",
                color = Caramel.copy(alpha = 0.9f),
                fontFamily = NazaninFont,
                fontSize = 19.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WoodenMenuButton(text = "شروع بازی", onClick = onStart)
                WoodenMenuButton(text = "تنظیمات", onClick = onSettings)
                WoodenMenuButton(text = "آموزش کامل", onClick = onTutorial)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WoodenMenuButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = tween(100),
        label = "button_press"
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
            .height(60.dp)
            .scale(scale)
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Caramel else Caramel.copy(alpha = 0.35f),
            fontFamily = NazaninFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}