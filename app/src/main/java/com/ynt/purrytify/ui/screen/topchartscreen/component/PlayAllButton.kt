package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayAllButton(
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayCircle,
            contentDescription = "Play all",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(60.dp)
        )
    }
}