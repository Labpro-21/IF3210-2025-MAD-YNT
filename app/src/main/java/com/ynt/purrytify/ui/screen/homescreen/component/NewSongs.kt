package com.ynt.purrytify.ui.screen.homescreen.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.models.Song

@Composable
fun NewSongs(
    songList: List<Song>,
    playSong : (Song) -> Unit,
    onSongsLoaded: (List<Song>?) -> Unit = {}
) {
    Column (
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "New Songs",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        NewSongsList(songList, playSong, onSongsLoaded)
    }
}

@Composable
fun NewSongsList(
    songList: List<Song>,
    playSong : (Song) -> Unit,
    onSongsLoaded: (List<Song>?) -> Unit = {}
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
                playSong = {
                    onSongsLoaded(songList)
                    songList.forEach { song ->
                        Log.d("Song", song.title.toString())
                    }
                    playSong(song)
                }
            )
        }
    }
}

@Composable
fun SongCard(
    song: Song,
    playSong: (Song) -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(170.dp)
            .clickable { playSong(song) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(song.image),
                contentDescription = song.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp) // hanya sudut atas yang bulat
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song.title ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist ?: "",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}