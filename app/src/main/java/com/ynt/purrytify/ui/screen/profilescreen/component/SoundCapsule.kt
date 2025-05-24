package com.ynt.purrytify.ui.screen.profilescreen.component

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.profilescreen.ProfileViewModel

@Composable
fun SoundCapsule(
    viewModel: ProfileViewModel
) {
    val timeListened by viewModel.timeListened.observeAsState(emptyList())
    val topSongs by viewModel.topSongs.observeAsState(emptyList())
    val topArtists by viewModel.topArtists.observeAsState(emptyList())
    Log.d("Time Listened Count", timeListened.size.toString())
    timeListened.forEach { value ->
        Text(
            value.timeListened.toString(),
            color = Color.White
        )
    }
}