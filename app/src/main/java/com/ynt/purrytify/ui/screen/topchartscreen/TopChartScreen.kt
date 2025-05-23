package com.ynt.purrytify.ui.screen.topchartscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper
import com.ynt.purrytify.ui.screen.homescreen.component.ChartBox
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.ui.screen.topchartscreen.component.SongList
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TopSongScreen(
    navController: NavController,
    viewModel: TopChartViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel,
    isRegion : Boolean,
    sessionManager: SessionManager,
    downloadHelper: DownloadHelper,
    showSongPlayerSheet: MutableState<Boolean>,
) {
    val topColor = Color(0xFF108B74)
    val bottomColor = Color(0xFF1E3264)
    LazyColumn() {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                topColor,
                                Color(0xFF000000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                ChartBox(
                    topText = "Top 50",
                    bottomText = "GLOBAL",
                    topColor = topColor,
                    bottomColor = bottomColor
                )

                BackButtonIcon(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                )
            }

            LaunchedEffect(Unit) {
                viewModel.loadTopSongs(isRegion = isRegion, sessionManager = sessionManager)
            }

            val onlineListSong by viewModel.onlineSongs.observeAsState(emptyList())

            LaunchedEffect(onlineListSong) {
                val listSong = viewModel.convertOnlineSongToSong(onlineListSong)
                if(isRegion){
                    playbackViewModel.syncOnline(listSong, viewModel.currentRegion.value ?: "")
                }
                else {
                    playbackViewModel.syncOnline(listSong, "GLOBAL")
                }
            }

            SongList(
                songList = onlineListSong,
                downloadHelper = downloadHelper,
                viewModel = viewModel,
                sessionManager = sessionManager,
                playSong = { selectedSong ->
                    if(playbackViewModel.currentSong?.id ?: null == selectedSong.id){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        playbackViewModel.setOnline(if (isRegion) viewModel.currentRegion.value ?: "" else "GLOBAL")
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        playbackViewModel.playSongById(selectedSong.id.toString())
                    }
                }
            )
        }
    }
}
