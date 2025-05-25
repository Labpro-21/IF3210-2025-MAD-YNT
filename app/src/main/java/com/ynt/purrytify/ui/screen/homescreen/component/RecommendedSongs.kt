package com.ynt.purrytify.ui.screen.homescreen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel

@Composable
fun RecommendedSongs(
    songList: List<Song>,
    showSongPlayerSheet: MutableState<Boolean>,
    playbackViewModel: PlaybackViewModel
) {
    Column (
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Recommended Songs",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

       RecommendedSongsList(songList,showSongPlayerSheet,playbackViewModel)
    }
}

@Composable
fun RecommendedSongsList(
    songList: List<Song>,
    showSongPlayerSheet: MutableState<Boolean>,
    playbackViewModel: PlaybackViewModel
) {
    LazyRow (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(songList) { song ->
            SongCard(
                song = song,
                playbackViewModel = playbackViewModel,
                showSongPlayerSheet = showSongPlayerSheet
            )
        }
    }
}