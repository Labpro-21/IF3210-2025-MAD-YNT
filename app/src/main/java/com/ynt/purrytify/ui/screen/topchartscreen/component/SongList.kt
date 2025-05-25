package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.topchartscreen.TopChartViewModel
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.downloadmanager.parseDurationToSeconds

@Composable
fun SongList(
    downloadHelper : DownloadHelper,
    songList : List<OnlineSong>,
    viewModel : TopChartViewModel,
    sessionManager: SessionManager,
    playSong : (Song) -> Unit,
    navController: NavController
) {
    Column (
        modifier = Modifier
            .padding(start = 10.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        songList.forEach() { song ->
            OneSong(
                song = song,
                downloadHelper = downloadHelper,
                viewModel = viewModel,
                sessionManager = sessionManager,
                playSong = playSong,
                navController = navController
            )
        }
    }
}

@Composable
fun OneSong(
    song : OnlineSong,
    downloadHelper: DownloadHelper,
    viewModel: TopChartViewModel,
    sessionManager: SessionManager,
    playSong: (Song) -> Unit,
    navController: NavController
) {
    val showOptionsSheet = remember { mutableStateOf(false) }
    val showShareSheet = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable { playSong(
                Song(
                    id = song.id,
                    title = song.title,
                    artist = song.artist,
                    owner = sessionManager.getUser(),
                    image = song.artwork,
                    audio = song.url,
                    duration = parseDurationToSeconds(song.duration)
                )
            ) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.artwork),
            contentDescription = song.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = song.title ?: "",
                color = Color.White,
                fontSize = 16.sp,
                maxLines = 1
            )
            Text(
                text = song.artist ?: "",
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                showOptionsSheet.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                tint = Color.White,
                contentDescription = "Menu lainnya"
            )
        }

        if (showOptionsSheet.value) {
            SongOptions(
                onDismiss = { showOptionsSheet.value = false },
                onDownload = {
                    downloadHelper.startDownload(
                        song = song,
                        viewModel = viewModel,
                        user = sessionManager.getUser()
                    )
                },
                songID = song.id,
                showShareSheetState = showShareSheet,
                closeSelf = { showOptionsSheet.value = false }
            )
        }

        if (showShareSheet.value) {
            ShareOptions(
                onDismiss = { showShareSheet.value = false },
                songID = song.id,
                songTitle = song.title,
                songArtist = song.artist,
                navController = navController
            )
        }
    }
}