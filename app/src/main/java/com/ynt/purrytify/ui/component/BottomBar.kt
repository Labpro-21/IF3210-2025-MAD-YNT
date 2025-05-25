package com.ynt.purrytify.ui.component

import android.content.res.Configuration
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
    configuration: Configuration,
    onClick: () -> Unit,
    ){
    Column {
        if(playbackViewModel.currentMediaId > -1) {
                Miniplayer(
                    playbackViewModel = playbackViewModel,
                    onClick = onClick,
                )
        }
        if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            CustomNavBar(navController)
        }
    }
}