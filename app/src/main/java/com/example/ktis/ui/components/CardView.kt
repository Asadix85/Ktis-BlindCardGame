package com.example.ktis.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import com.example.ktis.ui.theme.CardBack
import com.example.ktis.ui.theme.CardWhite
import com.example.ktis.ui.theme.HeartsRed

@Composable
fun CardView(
    card: Card?,
    faceUp: Boolean,
    modifier: Modifier = Modifier
) {
    val cardRotation = remember { Animatable(0f) }
    val cardYOffset = remember { Animatable(-180f) }
    val cardScale = remember { Animatable(0.88f) }
    val cardShake = remember { Animatable(0f) }

    LaunchedEffect(card) {
        if (card != null) {
            cardRotation.snapTo(0f)
            cardYOffset.snapTo(-180f)
            cardScale.snapTo(0.88f)
            cardShake.snapTo(0f)

            // پرتاب سریع کارت روی میز
            cardYOffset.animateTo(
                0f,
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                )
            )

            // ضربه
            cardScale.animateTo(
                1.08f,
                animationSpec = tween(70)
            )

            cardScale.animateTo(
                0.97f,
                animationSpec = tween(80)
            )

            cardScale.animateTo(
                1f,
                animationSpec = tween(100)
            )

            // لرزش خشن
            cardShake.animateTo(
                -7f,
                animationSpec = tween(35)
            )

            cardShake.animateTo(
                7f,
                animationSpec = tween(35)
            )

            cardShake.animateTo(
                -4f,
                animationSpec = tween(30)
            )

            cardShake.animateTo(
                4f,
                animationSpec = tween(30)
            )

            cardShake.animateTo(
                0f,
                animationSpec = tween(30)
            )
        }
    }

    LaunchedEffect(faceUp) {
        if (faceUp && card != null) {
            cardRotation.animateTo(
                180f,
                animationSpec = tween(
                    durationMillis = 420,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    val shape = RoundedCornerShape(18.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .graphicsLayer {
                rotationY = cardRotation.value
                translationY = cardYOffset.value
                translationX = cardShake.value
                scaleX = cardScale.value
                scaleY = cardScale.value
                cameraDistance = 18f * density
            }
            .border(
                1.dp,
                Color.LightGray,
                shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor =
                if (cardRotation.value <= 90f) {
                    CardBack
                } else {
                    CardWhite
                }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (cardRotation.value <= 90f) {
                FaceDownCard()
            } else if (card != null) {
                Box(
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f
                    }
                ) {
                    FaceUpCard(card)
                }
            }
        }
    }
}

@Composable
private fun FaceUpCard(card: Card) {
    val symbol = when (card.suit) {
        Suit.HEARTS -> "♥"
        Suit.DIAMONDS -> "♦"
        Suit.CLUBS -> "♣"
        Suit.SPADES -> "♠"
    }

    val color = when (card.suit) {
        Suit.HEARTS,
        Suit.DIAMONDS -> HeartsRed

        Suit.CLUBS,
        Suit.SPADES -> Color.Black
    }

    val rank = when (card.rank) {
        Rank.ACE -> "A"
        Rank.KING -> "K"
        Rank.QUEEN -> "Q"
        Rank.JACK -> "J"
        Rank.TEN -> "10"
        Rank.NINE -> "9"
        Rank.EIGHT -> "8"
        Rank.SEVEN -> "7"
        Rank.SIX -> "6"
        Rank.FIVE -> "5"
        Rank.FOUR -> "4"
        Rank.THREE -> "3"
        Rank.TWO -> "2"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$rank$symbol",
            color = color,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = color,
                fontSize = 64.sp
            )
        }

        Text(
            text = "$rank$symbol",
            color = color,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FaceDownCard() {
    Image(
        painter = painterResource(id = R.drawable.card_back),
        contentDescription = "پشت کارت",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                1.dp,
                Color.DarkGray,
                RoundedCornerShape(14.dp)
            )
    )
}