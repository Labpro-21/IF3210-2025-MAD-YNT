package com.ynt.purrytify.ui.screen.profilescreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun TimeListenedScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    sessionManager: SessionManager,
) {
    Text("Time Listened", color = Color.White)
}