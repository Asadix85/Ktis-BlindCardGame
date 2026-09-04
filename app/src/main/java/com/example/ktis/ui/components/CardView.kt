package com.example.ktis.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.example.ktis.ui.theme.CardWhite
import com.example.ktis.ui.theme.HeartsRed

@Composable
fun CardView(
    card: Card,
    isWinner: Boolean = false,
    throwDirection: Float = 0f,
    modifier: Modifier = Modifier
) {
    val offsetX =
        remember { Animatable(throwDirection * 420f) }

    val offsetY =
        remember { Animatable(180f) }

    val rotation =
        remember { Animatable(throwDirection * 14f) }

    val scale =
        remember { Animatable(0.72f) }

    val impact =
        remember { Animatable(1f) }

    LaunchedEffect(card) {

        offsetX.snapTo(throwDirection * 420f)
        offsetY.snapTo(180f)
        rotation.snapTo(throwDirection * 14f)
        scale.snapTo(0.72f)
        impact.snapTo(1f)

        offsetX.animateTo(
            0f,
            tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        offsetY.animateTo(
            0f,
            tween(
                durationMillis = 260,
                easing = FastOutSlowInEasing
            )
        )

        rotation.animateTo(
            0f,
            tween(220)
        )

        scale.animateTo(
            1.08f,
            tween(70)
        )

        scale.animateTo(
            0.96f,
            tween(70)
        )

        scale.animateTo(
            1f,
            tween(80)
        )
    }

    val shape =
        RoundedCornerShape(18.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
                scaleX = scale.value
                scaleY = scale.value
                cameraDistance = 18f * density
            }
            .border(
                width = if (isWinner) 4.dp else 1.dp,
                color =
                    if (isWinner) {
                        Color(0xFF20E070)
                    } else {
                        Color.LightGray
                    },
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (isWinner) 14.dp else 8.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FaceUpCard(card)
        }
    }
}

@Composable
private fun FaceUpCard(
    card: Card
) {
    val symbol =
        when (card.suit) {
            Suit.HEARTS -> "♥"
            Suit.DIAMONDS -> "♦"
            Suit.CLUBS -> "♣"
            Suit.SPADES -> "♠"
        }

    val color =
        when (card.suit) {
            Suit.HEARTS,
            Suit.DIAMONDS ->
                HeartsRed

            Suit.CLUBS,
            Suit.SPADES ->
                Color.Black
        }

    val rank =
        when (card.rank) {
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
            .padding(10.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "$rank$symbol",
            color = color,
            fontSize = 26.sp,
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
                fontSize = 58.sp
            )
        }

        Text(
            text = "$rank$symbol",
            color = color,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}