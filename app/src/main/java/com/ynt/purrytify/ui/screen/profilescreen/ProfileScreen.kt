package com.ynt.purrytify.ui.screen.profilescreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.ui.screen.profilescreen.component.EditProfileButton
import com.ynt.purrytify.ui.screen.profilescreen.component.LogoutButton
import com.ynt.purrytify.ui.screen.profilescreen.component.ProfileDetail
import com.ynt.purrytify.ui.screen.profilescreen.component.SoundCapsule
import com.ynt.purrytify.ui.screen.profilescreen.component.SoundCapsuleHeader
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    sessionManager: SessionManager,
    downloadHelper: DownloadHelper,
    isLoggedIn: MutableState<Boolean>
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile(sessionManager)
    }

    val result = viewModel.data.observeAsState().value
//    val scrollState = rememberScrollState()

    Scaffold (
        containerColor = Color.Black,
        topBar = {
            Text(
                text = "Profile",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(Color.Black)
                    .padding(
                        top = 30.dp,
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 12.dp,),
                fontSize = 25.sp
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            result?.onSuccess { data ->
                item {
                    ProfileDetail(data)
                }

                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }

                item {
                    EditProfileButton {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "photoURL",
                            data?.photoURL
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "location",
                            data?.location
                        )
                        navController.navigate("editProfile")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }

                item {
                    SongProfileDetail(viewModel)
                }

                item {
                    LogoutButton {
                        sessionManager.logout()
                        isLoggedIn.value = false
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF121212))
                            .padding(top = 10.dp)
                    ) {
                        Column() {
                            SoundCapsuleHeader(viewModel, downloadHelper)
                            SoundCapsule(viewModel, sessionManager)
                        }

                    }
                }

            } ?: run {
                item {
                    Text(
                        text = "Memuat data...",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SongProfileDetail(viewModel: ProfileViewModel) {
    val songCount = viewModel.countSong.observeAsState(0).value ?: 0
    val countLiked = viewModel.countLiked.observeAsState(0).value ?: 0
    val playedCount = viewModel.playedCount.observeAsState(0).value ?: 0

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$songCount",
                color = Color.White,
                fontSize = 20.sp,
            )
            Text("Songs",
                color = Color.White,
                fontSize = 15.sp,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$countLiked",
                color = Color.White,
                fontSize = 20.sp,)
            Text("Liked",
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$playedCount",
                color = Color.White,
                fontSize = 20.sp
            )
            Text("Listened",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}