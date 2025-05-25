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
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.ynt.purrytify.ui.screen.topchartscreen.component.BackButtonIcon
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun TimeListenedScreen(
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
                    text = "Time Listened",
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
                val time = viewModel.timeListenedPerMonth.observeAsState(0).value

                val styledText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("You listened to music for ")
                    }

                    withStyle(style = SpanStyle(color = Color.Yellow)) {
                        append("${time/60000} minutes")
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
            }

//            item {
//                val timeListenedPerDayList =
//                    viewModel.listTimeListened.observeAsState(emptyList()).value
//                val entries = timeListenedPerDayList.map {
//                    entryOf(
//                        it.day.toFloat(),
//                        it.totalTimeListened.toFloat()
//                    )
//                }
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .padding(16.dp)
//                        .background(Color.White)
//                ) {
//                    val chartEntryModelProducer = ChartEntryModelProducer(entries)
//                    Chart(
//                        chart = lineChart(),
//                        chartModelProducer = chartEntryModelProducer
//                    )
//                }
//            }
        }
    }
}