package com.ynt.purrytify.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomBar(
    navController: NavController,
    currentSong: Song?,
    xcurrentDuration: MutableStateFlow<Float>,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onSkip: () -> Unit,
    onClick: () -> Unit,
    ){
    Column {
        if(currentSong?.title?.isNotBlank() == true) {
            Miniplayer(
                currentSong = currentSong,
                onSkip = onSkip,
                onPlay = onPlay,
                onClick = onClick,
                isPlaying = isPlaying,
                xcurrentDuration = xcurrentDuration
            )
        }
        CustomNavBar(navController)
    }
}