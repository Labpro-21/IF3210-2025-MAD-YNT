package com.ynt.purrytify.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.utils.networksensing.ConnectivityViewModel
import com.ynt.purrytify.utils.networksensing.ConnectivityViewModelFactory

@Composable
fun ConnectivityStatusBanner(
    connectivityViewModel: ConnectivityViewModel = viewModel(
        factory = ConnectivityViewModelFactory(LocalContext.current)
    )
) {
    val isConnected by connectivityViewModel.isConnected.collectAsState(initial = true)
    if (!isConnected) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(8.dp)
        ) {
            Text(
                text = "No Internet Connection",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}