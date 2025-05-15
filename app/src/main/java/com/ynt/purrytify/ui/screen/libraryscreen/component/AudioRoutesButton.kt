package com.ynt.purrytify.ui.screen.libraryscreen.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ynt.purrytify.Screen
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AudioRoutesButton(navController: NavController){
    IconButton(onClick = {
        navController.navigate(Screen.AudioRouting.route)
    }) {
        Icon(
            Icons.Filled.Speaker,
            contentDescription = "Audio Routes",
            tint = Color.Green,
            modifier = Modifier.size(28.dp)
        )
    }

}