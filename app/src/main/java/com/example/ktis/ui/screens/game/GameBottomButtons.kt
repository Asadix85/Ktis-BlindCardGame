package com.example.ktis.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ktis.ui.theme.CarpetBrown
import com.example.ktis.ui.theme.CarpetBurgundy
import com.example.ktis.ui.theme.CarpetGold


@Composable
fun GameBottomButtons(
    onShuffle: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Button(
            onClick = onShuffle,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CarpetBrown,
                contentColor = CarpetGold
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {

            Text(
                text = "🔀 بر زدن کارت‌ها",
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CarpetBurgundy,
                contentColor = CarpetGold
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {

            Text(
                text = "بازگشت به منوی لوکال",
                fontWeight = FontWeight.Bold
            )
        }
    }
}