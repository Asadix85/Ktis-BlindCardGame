package com.example.ktis.ui.screens.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ktis.R
import com.example.ktis.ui.theme.CarpetGold


/*
 * ============================================================
 * دسته کارت پایین صفحه
 * ============================================================
 *
 * با یک لمس ساده، کارت رویی پرتاب می‌شه.
 */
@Composable
fun PlayerCardStack(
    remainingCards: Int,
    animateDraw: Boolean,
    enabled: Boolean,
    onThrow: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (remainingCards <= 0) {

        Spacer(
            modifier = Modifier.height(82.dp)
        )

        return
    }

    val cardWidth =
        GameConstants.HandCardWidth

    val cardHeight =
        GameConstants.HandCardHeight

    val maxStackHeight =
        GameConstants.HandMaxStackHeight

    val visibleCards =
        minOf(
            remainingCards,
            GameConstants.HandMaxVisibleCards
        )

    val spacing =
        if (visibleCards <= 1) {

            0.dp

        } else {

            (
                    (
                            maxStackHeight.value -
                                    cardHeight.value
                            ) /
                            (visibleCards - 1)
                    ).dp
        }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by
    interactionSource.collectIsPressedAsState()

    val pressScale by
    animateFloatAsState(
        targetValue =
            if (isPressed && enabled) {
                GameConstants.StackPressScale
            } else {
                1f
            },
        animationSpec =
            tween(GameConstants.StackPressDuration),
        label = "stack_press"
    )

    val throwHopOffset by
    animateFloatAsState(
        targetValue =
            if (animateDraw) {
                GameConstants.StackThrowHopOffsetDp
            } else {
                0f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    GameConstants.StackThrowHopDuration

                -18f at 90

                0f at GameConstants.StackThrowHopDuration
            },
        label = "stack_throw_hop"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(maxStackHeight)
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication =
                    ripple(
                        bounded = false,
                        color = CarpetGold
                    ),
                onClick = onThrow
            ),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .scale(pressScale)
                .offset(y = throwHopOffset.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    clip = false
                )
        ) {

            repeat(visibleCards) { index ->

                Image(
                    painter =
                        painterResource(
                            id = R.drawable.card_back
                        ),
                    contentDescription = "دسته کارت",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .offset(y = -(spacing * index))
                )
            }
        }

        CardCountLabel(count = remainingCards)
    }
}