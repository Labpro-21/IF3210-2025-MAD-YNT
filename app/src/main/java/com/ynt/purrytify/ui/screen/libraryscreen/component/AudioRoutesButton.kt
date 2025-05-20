package com.ynt.purrytify.ui.screen.libraryscreen.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ynt.purrytify.R
import com.ynt.purrytify.Screen

@Composable
fun AudioRoutesButton(navController: NavController){
    IconButton(onClick = {
        navController.navigate(Screen.AudioRouting.route)
    }) {
        Icon(
            Icons.Filled.Speaker,
            contentDescription = "Audio Routes",
            tint = colorResource(R.color.green),
            modifier = Modifier.size(28.dp)
        )
    }

}