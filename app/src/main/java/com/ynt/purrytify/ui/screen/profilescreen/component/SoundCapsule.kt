package com.ynt.purrytify.ui.screen.profilescreen.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.models.MaxStreak
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.profilescreen.ProfileViewModel
import com.ynt.purrytify.utils.auth.SessionManager
import java.time.Month

@Composable
fun SoundCapsule(
    viewModel: ProfileViewModel,
    sessionManager: SessionManager,
) {
    LaunchedEffect(Unit) {
        viewModel.getSoundCapsuleData(sessionManager)
    }

    val timeListened by viewModel.timeListened.observeAsState(emptyList())
    val topSongs by viewModel.topSongs.observeAsState(emptyList())
    val topArtists by viewModel.topArtists.observeAsState(emptyList())
    val listTopSong by viewModel.listTopSong.observeAsState(emptyList())
    val listTopArtist by viewModel.listTopArtist.observeAsState(emptyList())
    val longestStreakSong by viewModel.longestStreakSong.observeAsState(emptyList())
    Log.d("SoundCapsule", "Time Listened Size: ${timeListened.size}")
    Log.d("SoundCapsule", "ListTopSong Size: ${listTopSong.size}")
    Log.d("SoundCapsule", "ListTopArtist Size: ${listTopArtist.size}")
    Log.d("SoundCapsule", "LongestStreakSong Size: ${longestStreakSong.size}")
    Log.d("SoundCapsule", "TopSongs Size: ${topSongs.size}")
    Log.d("SoundCapsule", "TopArtists Size: ${topArtists.size}")

    if (timeListened.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Data Available",
                color = Color.LightGray,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        for (i in timeListened.indices.reversed()) {
            Text(
                text = "${
                    Month.of(timeListened[i].month).name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                } ${timeListened[i].year}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            )

            if (listTopSong.isNotEmpty() && listTopArtist.isNotEmpty()) {
                SoundCapsuleGrid(
                    timeListened = timeListened[i].timeListened,
                    songTitle = listTopSong[i].title.toString(),
                    artistName = listTopArtist[i].artist.toString(),
                    songImage = listTopSong[i].image.toString(),
                    artistImage = listTopArtist[i].image.toString(),
                    longestStreakSong = longestStreakSong?.get(i),
                )
            }
        }
    }
}