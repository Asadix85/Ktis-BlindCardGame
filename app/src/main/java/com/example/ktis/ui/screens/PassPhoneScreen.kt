package com.example.ktis.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium


@Composable
fun PassPhoneScreen(
    playerName: String,
    centerPile: List<PlayedCard>,
    onContinue: () -> Unit
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
                    horizontal = 28.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (centerPile.isNotEmpty()) {

                Text(
                    text = "کارت‌های روی زمین",
                    color = Caramel,
                    fontFamily = NazaninFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    centerPile.forEach { playedCard ->

                        CardView(
                            card = playedCard.card,
                            modifier = Modifier.size(
                                width = 78.dp,
                                height = 115.dp
                            )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }

            Text(
                text = "📱",
                fontSize = 80.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "نوبت توئه",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                WoodMedium.copy(alpha = 0.92f),
                                WoodDark.copy(alpha = 0.92f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Caramel.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 22.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = playerName,
                        color = Caramel,
                        fontFamily = NazaninFont,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "گوشی را به $playerName بده",
                        color = Caramel.copy(alpha = 0.9f),
                        fontFamily = NazaninFont,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "مطمئن شو بازیکن قبلی کارت خودش را ندیده است.",
                        color = Caramel.copy(alpha = 0.65f),
                        fontFamily = NazaninFont,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            WoodenButton(
                text = "آماده‌ام — ادامه",
                onClick = onContinue,
                highlighted = true
            )
        }
    }
}