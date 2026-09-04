package com.example.ktis.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ktis.R
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import kotlinx.coroutines.launch

@Composable
fun CardView(
    card: Card,
    isWinner: Boolean = false,
    throwDirection: Float = 0f,
    animateThrow: Boolean = false,
    modifier: Modifier = Modifier
) {

    val resourceId =
        when (card.suit) {

            Suit.CLUBS ->
                when (card.rank) {
                    Rank.TWO -> R.drawable.card_2_clubs
                    Rank.THREE -> R.drawable.card_3_clubs
                    Rank.FOUR -> R.drawable.card_4_clubs
                    Rank.FIVE -> R.drawable.card_5_clubs
                    Rank.SIX -> R.drawable.card_6_clubs
                    Rank.SEVEN -> R.drawable.card_7_clubs
                    Rank.EIGHT -> R.drawable.card_8_clubs
                    Rank.NINE -> R.drawable.card_9_clubs
                    Rank.TEN -> R.drawable.card_10_clubs
                    Rank.JACK -> R.drawable.card_jack_clubs
                    Rank.QUEEN -> R.drawable.card_queen_clubs
                    Rank.KING -> R.drawable.card_king_clubs
                    Rank.ACE -> R.drawable.card_ace_clubs
                }

            Suit.DIAMONDS ->
                when (card.rank) {
                    Rank.TWO -> R.drawable.card_2_diamonds
                    Rank.THREE -> R.drawable.card_3_diamonds
                    Rank.FOUR -> R.drawable.card_4_diamonds
                    Rank.FIVE -> R.drawable.card_5_diamonds
                    Rank.SIX -> R.drawable.card_6_diamonds
                    Rank.SEVEN -> R.drawable.card_7_diamonds
                    Rank.EIGHT -> R.drawable.card_8_diamonds
                    Rank.NINE -> R.drawable.card_9_diamonds
                    Rank.TEN -> R.drawable.card_10_diamonds
                    Rank.JACK -> R.drawable.card_jack_diamonds
                    Rank.QUEEN -> R.drawable.card_queen_diamonds
                    Rank.KING -> R.drawable.card_king_diamonds
                    Rank.ACE -> R.drawable.card_ace_diamonds
                }

            Suit.HEARTS ->
                when (card.rank) {
                    Rank.TWO -> R.drawable.card_2_hearts
                    Rank.THREE -> R.drawable.card_3_hearts
                    Rank.FOUR -> R.drawable.card_4_hearts
                    Rank.FIVE -> R.drawable.card_5_hearts
                    Rank.SIX -> R.drawable.card_6_hearts
                    Rank.SEVEN -> R.drawable.card_7_hearts
                    Rank.EIGHT -> R.drawable.card_8_hearts
                    Rank.NINE -> R.drawable.card_9_hearts
                    Rank.TEN -> R.drawable.card_10_hearts
                    Rank.JACK -> R.drawable.card_jack_hearts
                    Rank.QUEEN -> R.drawable.card_queen_hearts
                    Rank.KING -> R.drawable.card_king_hearts
                    Rank.ACE -> R.drawable.card_ace_hearts
                }

            Suit.SPADES ->
                when (card.rank) {
                    Rank.TWO -> R.drawable.card_2_spades
                    Rank.THREE -> R.drawable.card_3_spades
                    Rank.FOUR -> R.drawable.card_4_spades
                    Rank.FIVE -> R.drawable.card_5_spades
                    Rank.SIX -> R.drawable.card_6_spades
                    Rank.SEVEN -> R.drawable.card_7_spades
                    Rank.EIGHT -> R.drawable.card_8_spades
                    Rank.NINE -> R.drawable.card_9_spades
                    Rank.TEN -> R.drawable.card_10_spades
                    Rank.JACK -> R.drawable.card_jack_spades
                    Rank.QUEEN -> R.drawable.card_queen_spades
                    Rank.KING -> R.drawable.card_king_spades
                    Rank.ACE -> R.drawable.card_ace_spades
                }
        }

    val startX =
        if (animateThrow) {
            throwDirection * 220f
        } else {
            0f
        }

    val startY =
        if (animateThrow) {
            when {
                throwDirection == 0f -> 220f
                throwDirection < 0f -> 80f
                else -> 80f
            }
        } else {
            0f
        }

    val offsetX =
        remember(card, animateThrow) {
            Animatable(startX)
        }

    val offsetY =
        remember(card, animateThrow) {
            Animatable(startY)
        }

    val rotation =
        remember(card, animateThrow) {
            Animatable(
                if (animateThrow) {
                    throwDirection * 12f
                } else {
                    0f
                }
            )
        }

    LaunchedEffect(card, animateThrow) {

        if (animateThrow) {

            launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        tween(
                            durationMillis = 500,
                            easing =
                                FastOutSlowInEasing
                        )
                )
            }

            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        tween(
                            durationMillis = 500,
                            easing =
                                FastOutSlowInEasing
                        )
                )
            }

            launch {
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        tween(
                            durationMillis = 450
                        )
                )
            }
        }
    }

    Image(
        painter =
            painterResource(resourceId),

        contentDescription =
            "${card.rank} of ${card.suit}",

        contentScale =
            ContentScale.Fit,

        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .graphicsLayer {

                    translationX =
                        offsetX.value

                    translationY =
                        offsetY.value

                    rotationZ =
                        rotation.value
                }
                .then(
                    if (isWinner) {

                        Modifier
                            .shadow(
                                elevation = 14.dp,
                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    )
                            )
                            .border(
                                width = 3.dp,
                                color =
                                    Color(0xFFFFD700),
                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    )
                            )

                    } else {
                        Modifier
                    }
                )
    )
}