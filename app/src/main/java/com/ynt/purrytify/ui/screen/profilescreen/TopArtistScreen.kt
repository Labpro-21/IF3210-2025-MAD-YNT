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
fun TopArtistScreen(
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
                    text = "Top Artists",
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
                Text(
                    text = "April 2025",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(15.dp))
            }

            item {
                val artistCount = viewModel.artistPerMonth.observeAsState(0).value

                val styledText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("You listened to ")
                    }

                    withStyle(style = SpanStyle(color = Color(0xFF7EA6F6))) {
                        append("$artistCount artists")
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
                val listTopSong = viewModel.listTopTenArtist.observeAsState(emptyList()).value

                if (listTopSong.isNotEmpty())
                    listTopSong.forEachIndexed { index, song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = (index+1).toString(),
                                color = Color(0xFF7EA6F6),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = song.artists,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 3,
                                modifier = Modifier.weight(3f)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Image(
                                painter = rememberAsyncImagePainter(song.image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(CircleShape)
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