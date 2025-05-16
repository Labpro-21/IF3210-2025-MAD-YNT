package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BackButtonIcon(modifier: Modifier) {
    IconButton(
        onClick = { /* TODO: aksi back */ },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}