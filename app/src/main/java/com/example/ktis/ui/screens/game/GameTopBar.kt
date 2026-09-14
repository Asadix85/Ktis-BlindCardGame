package com.example.ktis.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.CarpetGold


@Composable
fun GameTopBar(
    roundNumber: Int,
    totalCollectedCards: Int,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "دست $roundNumber",
            color = CarpetGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "جمع‌شده: $totalCollectedCards",
            color = CarpetGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}