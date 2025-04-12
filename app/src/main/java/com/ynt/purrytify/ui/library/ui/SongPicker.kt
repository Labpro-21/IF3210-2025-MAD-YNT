package com.ynt.purrytify.ui.library.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.R
import com.ynt.purrytify.ui.library.utils.getFileNameFromUri

@Composable
fun SongPicker(
    onSongPicked: (Uri?) -> Unit,
    context: Context,
    songUri: Uri?
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onSongPicked(uri)
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clickable { launcher.launch("audio/*") }
    ){
        if (songUri != null) {
            val fileName = getFileNameFromUri(context, songUri)
            Text(
                text = fileName,
                color = Color.White,
                fontSize = 14.sp
            )
        } else {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.choose_song),
                contentDescription = "Choose Song",
                modifier = Modifier
                    .size(96.dp)
            )
        }
    }
}