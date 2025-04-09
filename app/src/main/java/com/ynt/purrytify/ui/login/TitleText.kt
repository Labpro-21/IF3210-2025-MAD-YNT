package com.ynt.purrytify.ui.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun TitleText(modifier: Modifier = Modifier) {
    Text(
        text = "Millions of Songs. \nOnly on Purritify.",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier
    )
}