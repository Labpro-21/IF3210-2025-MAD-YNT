package com.ynt.purrytify.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.ynt.purrytify.PlayerState
import com.ynt.purrytify.models.Song

@Composable
fun BottomBar(
    navController: NavController,
    currentSong: MutableState<Song?>,
    isPlaying: MutableState<PlayerState>,
    onPlay: () -> Unit,
    onSkip: () -> Unit,
    onClick: () -> Unit,
    ){
    Column {
        if(currentSong.value!=null) {
            Miniplayer(
                currentSong = currentSong,
                onSkip = onSkip,
                onPlay = onPlay,
                onClick = onClick,
                isPlaying = isPlaying
            )
        }
        CustomNavBar(navController)
    }
}