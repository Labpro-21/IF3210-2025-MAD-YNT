package com.ynt.purrytify.ui.screen.homescreen.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.models.Song
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel

@Composable
fun RecentlyPlayed(
    songList: List<Song>,
    playbackViewModel: PlaybackViewModel,
) {
    Column (
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Recently Played",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        SongListVertical(songList,playbackViewModel)
    }
}

@Composable
fun SongListVertical(
    songList: List<Song>,
    playbackViewModel: PlaybackViewModel
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        songList.forEach { song ->
            SongListItem(
                song = song,
                playbackViewModel = playbackViewModel
            )
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    playbackViewModel: PlaybackViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable {
                playbackViewModel.setLocal()
                playbackViewModel.playSongById(song.id.toString())
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.image),
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
    }
}