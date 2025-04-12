package com.ynt.purrytify.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NewSongs() {
    Column (
        modifier = Modifier
            .padding(30.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "New Songs",
            color = Color.White
        )

        Row {
            Text(text = "new songs here",
                color = Color.White
            )
        }
    }
}