package com.ynt.purrytify.ui.screen.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.ui.home.HomeViewModel
import com.ynt.purrytify.ui.screen.homescreen.component.NewSongs
import com.ynt.purrytify.ui.screen.homescreen.component.RecentlyPlayed
import com.ynt.purrytify.ui.screen.homescreen.component.TopCharts
import com.ynt.purrytify.utils.auth.SessionManager

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

    LazyColumn (
        horizontalAlignment = Alignment.Start
    ) {
        item {
            TopCharts(
                onGLobalClick = {
                    navController.navigate("topGlobalCharts")
                },
                onRegionClick = {
                    navController.navigate("topRegionCharts")
                }
            )
        }

        item {
            NewSongs(songsList)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            RecentlyPlayed(recentlySong)
        }
    }
}