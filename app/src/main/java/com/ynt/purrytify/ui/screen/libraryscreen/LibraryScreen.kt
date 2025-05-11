package com.ynt.purrytify.ui.screen.libraryscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun LibraryScreen(navController: NavController){
    val libraryViewModel: LibraryViewModel = viewModel()
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = "Testing",
            fontSize = 60.sp,
            color = Color.White
        )
    }
}