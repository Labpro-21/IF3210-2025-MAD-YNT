package com.ynt.purrytify.ui.screen.topchartscreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper
import com.ynt.purrytify.ui.screen.topchartscreen.component.ChartBox
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.ui.screen.topchartscreen.component.DownloadAllButton
import com.ynt.purrytify.ui.screen.topchartscreen.component.DownloadPlaySection
import com.ynt.purrytify.ui.screen.topchartscreen.component.PlayAllButton
import com.ynt.purrytify.ui.screen.topchartscreen.component.SongList
import com.ynt.purrytify.utils.auth.SessionManager
import java.util.Locale

@Composable
fun TopChartScreen(
    navController: NavController,
    viewModel: TopChartViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel,
    isRegion : Boolean,
    sessionManager: SessionManager,
    downloadHelper: DownloadHelper,
    showSongPlayerSheet: MutableState<Boolean>,
) {
    var topColor = Color(0xFF108B74)
    var bottomColor = Color(0xFF1E3264)
    if (isRegion) {
        topColor = Color(0xFFF16D7A)
        bottomColor = Color(0xFFEC1E32)
    }
    val onlineListSong by viewModel.onlineSongs.observeAsState(emptyList())
    val context = LocalContext.current

    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
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

                var topChartText = ""

                if (isRegion) {
                    topChartText = "Your daily update of the most played tracks right now - ${Locale("", viewModel.currentRegion.value.toString()).getDisplayCountry(Locale.ENGLISH)}"
                } else {
                    topChartText = "Your daily update of the most played tracks right now - Global"
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {

                    Text(
                        text = topChartText,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )

                    DownloadPlaySection(
                        onDownloadAll = {
                            onlineListSong.forEach { song ->
                                downloadHelper.startDownload(
                                    song = song,
                                    viewModel = viewModel,
                                    user = sessionManager.getUser()
                                )
                            }
                            Toast.makeText(context, "Downloading all song", Toast.LENGTH_SHORT).show()
                        },
                        onPlayAll = {
                            playbackViewModel.setOnline(if (isRegion) viewModel.currentRegion.value ?: "" else "GLOBAL")
                            playbackViewModel.playSongById(onlineListSong[0].id.toString())
                        }
                    )
                }
            }

            LaunchedEffect(Unit) {
                viewModel.loadTopSongs(isRegion = isRegion, sessionManager = sessionManager)
            }


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
                },
                navController = navController
            )
        }
    }
}
