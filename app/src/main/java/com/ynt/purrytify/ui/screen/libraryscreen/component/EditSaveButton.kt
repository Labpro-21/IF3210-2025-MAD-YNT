package com.ynt.purrytify.ui.screen.libraryscreen.component

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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.libraryscreen.utils.editSaveButtonOnClick
import com.ynt.purrytify.utils.queue.QueueManager
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSaveButton(
    libraryViewModel: LibraryViewModel,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowPopupSong: (Boolean) -> Unit,
    context: Context,
    song: Song,
    title: String,
    artist:String,
    imageUri: Uri?,
    queueManager: QueueManager,
    currentSong: MutableState<Song?>
) {
    val isButtonEnabled = title.isNotBlank() && artist.isNotBlank()
    Button(
        colors = ButtonDefaults.buttonColors(colorResource(R.color.green)),
        enabled = isButtonEnabled,
        onClick = {
            editSaveButtonOnClick(
                context = context,
                libraryViewModel = libraryViewModel,
                title = title,
                artist = artist,
                coroutineScope = coroutineScope,
                sheetState = sheetState,
                setShowPopupSong = setShowPopupSong,
                imageUri = imageUri,
                song = song,
                queueManager = queueManager,
                currentSong = currentSong
            )
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
