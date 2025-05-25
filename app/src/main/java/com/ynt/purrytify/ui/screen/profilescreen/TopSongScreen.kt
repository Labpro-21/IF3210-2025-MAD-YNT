package com.ynt.purrytify.ui.screen.profilescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun TopSongScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    sessionManager: SessionManager,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .background(Color.Black),
            ) {
                BackButtonIcon(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                )

                Text(
                    text = "Top Songs",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = 15.sp
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 22.dp, end = 22.dp, bottom = 16.dp)
        ) {
            item {
                val liveMonth = viewModel.monthLive.observeAsState("").value
                val liveYear = viewModel.yearLive.observeAsState(0).value
                Text(
                    text = "$liveMonth $liveYear",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(15.dp))
            }

            item {
                val songCount = viewModel.songPerMonth.observeAsState(0).value

                val styledText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("You played ")
                    }

                    withStyle(style = SpanStyle(color = Color.Yellow)) {
                        append("$songCount different songs")
                    }

                    withStyle(style = SpanStyle(color = Color.White)) {
                        append(" this month.")
                    }
                }

                Text(
                    text = styledText,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))

            }

            item {
                val listTopSong = viewModel.listTopTenSong.observeAsState(emptyList()).value

                if (listTopSong.isNotEmpty())
                    listTopSong.forEachIndexed { index, song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = (index+1).toString(),
                                color = Color.Yellow,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(
                                modifier = Modifier.weight(3f)
                            ) {
                                Text(
                                    text = song.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 3
                                )
                                Text(
                                    text = song.artists,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 3
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Image(
                                painter = rememberAsyncImagePainter(song.image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
            }
        }
    }
}