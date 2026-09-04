package com.example.ktis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.ui.theme.Gold
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun PassPhoneScreen(
    playerName: String,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "📱",
            fontSize = 80.sp
        )

        Spacer(Modifier.size(24.dp))

        Text(
            text = "نوبت توئه",
            style = MaterialTheme.typography.headlineMedium,
            color = Gold
        )

        Spacer(Modifier.size(10.dp))

        Text(
            text = playerName,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(16.dp))

        Text(
            text = "گوشی را به $playerName بده",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "مطمئن شو بازیکن قبلی کارت خودش را ندیده است.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.size(32.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "آماده‌ام — ادامه",
                color = androidx.compose.ui.graphics.Color.Black
            )
        }
    }
}