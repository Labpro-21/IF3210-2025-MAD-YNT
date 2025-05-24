package com.ynt.purrytify.ui.screen.homescreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.home.HomeViewModel
import com.ynt.purrytify.ui.screen.homescreen.component.NewSongs
import com.ynt.purrytify.ui.screen.homescreen.component.RecentlyPlayed
import com.ynt.purrytify.ui.screen.homescreen.component.TopCharts
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HomeScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: HomeViewModel = viewModel(),
    onSongsLoaded: (List<Song>?) -> Unit = {},
    currentSong: MutableStateFlow<Song>?,
    showSongPlayerSheet: MutableState<Boolean>,
    onPlay: (song: Song)->Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadHome(sessionManager)
    }

    val songsList = viewModel.songList.observeAsState(emptyList()).value
    val recentlySong = viewModel.songListRecently.observeAsState(emptyList()).value

    LazyColumn (
        horizontalAlignment = Alignment.Start
    ) {
        item {
            TopCharts(
                onGLobalClick = {
                    navController.navigate("topGlobalCharts")
                },
                onRegionClick = {
                    navController.navigate("topRegionCharts")
                },
                region = sessionManager.getProfile()["location"] ?: "ID"
            )
        }

        item {
            NewSongs(
                songList = songsList,
                playSong = { selectedSong ->
                    if(currentSong?.value?.id ?: null == selectedSong.id){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        onPlay(selectedSong)
                    }
                },
                onSongsLoaded = onSongsLoaded
            )
        }

        item {
            Spacer(modifier = Modifier.height(5.dp))
        }

        item {
            RecentlyPlayed(
                songList = recentlySong,
                playSong = { selectedSong ->
                    if(currentSong?.value?.id ?: null == selectedSong.id){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        onPlay(selectedSong)
                    }
                },
                onSongsLoaded = onSongsLoaded
            )
        }
    }
}