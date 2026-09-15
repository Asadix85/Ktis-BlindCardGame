package com.example.ktis.ui.screens.game

import androidx.compose.ui.unit.dp

object GameConstants {

    val CenterCardBoxWidth = 84.dp
    val CenterCardBoxHeight = 122.dp
    val CenterCardWidth = 72.dp

    val HandCardWidth = 62.dp
    val HandCardHeight = 88.dp
    val HandMaxStackHeight = 92.dp
    val HandMaxVisibleCards = 18

    const val BoardRadiusFactor = 0.30f
    const val CardLandingRadiusFactor = 1.16f
    const val ThrowStartRadiusFactor = 1.95f

    const val TableRotationDuration = 620
    const val RotationDelayMillis = 900L
    const val DropAnimationDuration = 420
    const val WinnerScaleDuration = 520
    const val TieScaleDuration = 500
    const val StackPressDuration = 100
    const val StackThrowHopDuration = 240
    const val AIDelayMinMillis = 500L
    const val AIDelayMaxMillis = 1200L
    const val DropStartScale = 0.55f
    const val DropEndScale = 1f
    const val WinnerScale = 1.08f
    const val TieScale = 1.06f
    const val StackPressScale = 0.94f
    const val StackThrowHopOffsetDp = -16f
}