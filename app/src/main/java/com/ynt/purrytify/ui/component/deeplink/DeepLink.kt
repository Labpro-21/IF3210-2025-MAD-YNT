package com.ynt.purrytify.ui.component.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel

@Composable
fun DeepLinkScreen(
    songId: String,
    viewModel: DeepLinkViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel,
    showSongPlayerSheet: MutableState<Boolean>
) {
    LaunchedEffect(Unit) {
        viewModel.loadSong(songId)
    }
    val onlineSong by viewModel.onlineSong.collectAsState()
    LaunchedEffect(onlineSong) {
        if (onlineSong == null) {
            return@LaunchedEffect
        }
        val song = viewModel.convertOnlineSongToSong(onlineSong)
        if (song == null) {
            return@LaunchedEffect
        }
        playbackViewModel.syncOnline(listOf(song), "none")
        playbackViewModel.setOnline("none")
        playbackViewModel.play()
        showSongPlayerSheet.value = true
    }
}
