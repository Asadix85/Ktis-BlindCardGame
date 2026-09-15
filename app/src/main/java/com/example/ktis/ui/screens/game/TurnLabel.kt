package com.example.ktis.ui.screens.game

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.CarpetGold

@Composable
fun TurnLabel(
    playerName: String,
    isAI: Boolean,
    modifier: Modifier = Modifier
) {

    val displayName =
        if (isAI) {
            "$playerName 🤖"
        } else {
            playerName
        }

    Text(
        text = "نوبت: $displayName",
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = CarpetGold,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
}