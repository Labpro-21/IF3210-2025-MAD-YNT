package com.ynt.purrytify.ui.screen.libraryscreen.component

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.R
import com.ynt.purrytify.ui.screen.libraryscreen.utils.getFileNameFromUri


@Composable
fun SongPicker(
    onSongPicked: (Uri?) -> Unit,
    context: Context,
    songUri: Uri?,
    title: MutableState<String>,
    artists: MutableState<String>,
    duration: MutableState<Int>
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
            val fileName = getFileNameFromUri(context,songUri)
            val retriever = MediaMetadataRetriever()
            context.contentResolver.openFileDescriptor(songUri, "r")?.use { pfd ->
                retriever.setDataSource(pfd.fileDescriptor)
            }
            title.value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()
            artists.value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
            duration.value =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
            val image = retriever.embeddedPicture
            Log.d("IMAGE EXTRACT","$image")
            retriever.release()
            Text(
                text = fileName,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(96.dp)
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