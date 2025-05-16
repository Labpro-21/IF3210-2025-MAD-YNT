package com.ynt.purrytify.ui.screen.topchartscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.ui.home.HomeViewModel
import com.ynt.purrytify.ui.screen.homescreen.component.ChartBox
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.ui.screen.topchartscreen.component.SongList
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun TopSongScreen(
    navController: NavController,
    viewModel: TopChartViewModel = viewModel(),
    isRegion : Boolean,
    sessionManager: SessionManager,
) {
    val topColor = Color(0xFF108B74)
    val bottomColor = Color(0xFF1E3264)
    LazyColumn() {
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
                ChartBox(
                    topText = "Top 50",
                    bottomText = "GLOBAL",
                    topColor = topColor,
                    bottomColor = bottomColor
                )

                BackButtonIcon(modifier = Modifier.align(Alignment.TopStart).padding(10.dp))
            }

            LaunchedEffect(Unit) {
                viewModel.loadTopSongs(isRegion = isRegion, sessionManager = sessionManager)
            }

            val onlineListSong by viewModel.onlineSongs.observeAsState(emptyList())

            SongList(onlineListSong)
        }


    }
}
