package com.ynt.purrytify.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomBar(
    navController: NavController,
    playbackViewModel: PlaybackViewModel,
    onClick: () -> Unit,
    ){
    Column {
        if(playbackViewModel.currentMediaId > -1) {
            Miniplayer(
                playbackViewModel = playbackViewModel,
                onClick = onClick,
            )
        }
        CustomNavBar(navController)
    }
}