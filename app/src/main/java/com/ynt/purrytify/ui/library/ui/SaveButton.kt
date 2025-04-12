package com.ynt.purrytify.ui.library.ui

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.ynt.purrytify.R
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.ui.library.LibraryViewModel
import com.ynt.purrytify.ui.library.copyUriToExternalStorage
import com.ynt.purrytify.ui.library.copyUriToStorage
import com.ynt.purrytify.ui.library.getFileNameFromUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveButton(
    title: String,
    artist: String,
    imageUri: Uri?,
    songUri: Uri?,
    songOwner: String,
    libraryViewModel: LibraryViewModel,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowPopupSong: (Boolean) -> Unit,
    context: Context
) {
    val isButtonEnabled = title.isNotBlank() && artist.isNotBlank() && songUri != null
    Button(
        colors = ButtonDefaults.buttonColors(colorResource(R.color.green)),
        enabled = isButtonEnabled,
        onClick = {
            if (songUri != null) {
                val savedSongUri = copyUriToExternalStorage(context,songUri,
                    getFileNameFromUri(context, songUri)
                )
                val savedImageUri = if(imageUri!=null) copyUriToStorage(context,imageUri) else null
                libraryViewModel.insert(
                    Song(
                        title = title,
                        artist = artist,
                        owner = songOwner,
                        image = savedImageUri.toString(),
                        audio = savedSongUri.toString()
                    )
                )
            }
            coroutineScope.launch {
                sheetState.hide()
                setShowPopupSong(false)
            }
        },
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Save",
            color = Color.White
        )
    }
}
