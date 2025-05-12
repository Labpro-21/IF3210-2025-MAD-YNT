package com.ynt.purrytify.ui.screen.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.home.HomeViewModel
import com.ynt.purrytify.ui.screen.homescreen.component.NewSongs
import com.ynt.purrytify.ui.screen.homescreen.component.RecentlyPlayed
import com.ynt.purrytify.utils.SessionManager

@Composable
fun HomeScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadNewSongs(sessionManager)
    }

    val songsList = viewModel.songList.observeAsState(emptyList()).value
    val recentlySong = viewModel.songListRecently.observeAsState(emptyList()).value

    Column (
        modifier = Modifier
            .padding(30.dp),
        horizontalAlignment = Alignment.Start
    ) {
        NewSongs(songsList)

        Spacer(modifier = Modifier.height(20.dp))

        RecentlyPlayed(recentlySong)
    }
}