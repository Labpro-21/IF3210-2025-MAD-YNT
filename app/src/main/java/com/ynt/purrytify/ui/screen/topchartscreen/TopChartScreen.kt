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
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper
import com.ynt.purrytify.ui.screen.topchartscreen.component.ChartBox
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.ui.screen.topchartscreen.component.SongList
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TopSongScreen(
    navController: NavController,
    viewModel: TopChartViewModel = viewModel(),
    isRegion : Boolean,
    sessionManager: SessionManager,
    downloadHelper: DownloadHelper,
    currentSong: MutableStateFlow<Song>?,
    showSongPlayerSheet: MutableState<Boolean>,
    onPlay: (song: Song)->Unit,
    onSongsLoaded: (List<Song>?) -> Unit = {},
) {
    var topColor = Color(0xFF108B74)
    var bottomColor = Color(0xFF1E3264)
    if (isRegion) {
        topColor = Color(0xFFF16D7A)
        bottomColor = Color(0xFFEC1E32)
    }

    LazyColumn {
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
                if (isRegion) {
                    ChartBox(
                        topText = "Top 10",
                        bottomText = sessionManager.getProfile()["location"] ?: "",
                        topColor = topColor,
                        bottomColor = bottomColor
                    )
                } else {
                    ChartBox(
                        topText = "Top 50",
                        bottomText = "GLOBAL",
                        topColor = topColor,
                        bottomColor = bottomColor
                    )
                }

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
                val listSong = viewModel.convertOnlineSongToSong()
                if (listSong != null) {
                    listSong.forEach { song ->
                        song.title?.let { Log.d("Online Song", it) }
                        song.audio?.let { Log.d("Online Song", it) }
                    }
                }
                onSongsLoaded(listSong)
                Log.d("Testingg", "here")
            }

            SongList(
                songList = onlineListSong,
                downloadHelper = downloadHelper,
                viewModel = viewModel,
                sessionManager = sessionManager,
                playSong = { selectedSong ->
                    if(currentSong?.value?.id ?: null == selectedSong.id){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        onPlay(selectedSong)
                    }
                }
            )
        }


    }
}
