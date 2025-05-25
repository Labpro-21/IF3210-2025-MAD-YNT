package com.ynt.purrytify.ui.screen.profilescreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.ui.screen.profilescreen.ProfileViewModel
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper

@Composable
fun SoundCapsuleHeader(
    viewModel: ProfileViewModel,
    downloadHelper: DownloadHelper
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Your Sound Capsule",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = {
                downloadHelper.saveToCsv(viewModel.getCsv())
            },
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowCircleDown,
                contentDescription = "Download CSV",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}