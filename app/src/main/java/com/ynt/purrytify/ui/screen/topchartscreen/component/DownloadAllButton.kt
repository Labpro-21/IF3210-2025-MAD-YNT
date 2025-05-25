package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.sharp.ArrowCircleDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DownloadAllButton(
    onClick: () -> Unit ={},
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowCircleDown,
            contentDescription = "Download all",
            tint = Color.Gray,
            modifier = Modifier.size(40.dp)
        )
    }
}