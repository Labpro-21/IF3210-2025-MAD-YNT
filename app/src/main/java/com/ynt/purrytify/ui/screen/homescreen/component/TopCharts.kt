package com.ynt.purrytify.ui.screen.homescreen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopCharts(
    onGLobalClick : () -> Unit,
    onRegionClick : () -> Unit
) {
    Column {
        Text(text = "Charts",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row (
            modifier = Modifier.padding(start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopChartsBox(
                topText = "Top 50",
                bottomText = "GLOBAL",
                topColor = Color(0xFF108B74),
                bottomColor = Color(0xFF1E3264),
                desc = "Your daily update of the most played tracks right now - Global",
                onClick = onGLobalClick
            )

            TopChartsBox(
                topText = "Top 10",
                bottomText = "ID",
                topColor = Color(0xFFF16D7A),
                bottomColor = Color(0xFFEC1E32),
                desc = "Your daily update of the most played tracks right now - Indonesia",
                onClick = onRegionClick
            )
        }

    }
}