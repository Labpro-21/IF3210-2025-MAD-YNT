package com.ynt.purrytify.ui.component.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.Screen
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun DeepLinkScreen(
    songId: String,
    viewModel: DeepLinkViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel,
    showSongPlayerSheet: MutableState<Boolean>,
    navController: NavController
) {
    val context = LocalContext.current
    val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val sessionManager = SessionManager(context)
        val loggedIn = sessionManager.isLoggedIn()
        if (!loggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            isLoggedIn.value = false
        } else {
            isLoggedIn.value = true
        }
    }
    if (isLoggedIn.value != true) return
    playbackViewModel.kill()
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
