package com.ynt.purrytify.ui.screen.libraryscreen.utils

import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.MutableState
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.utils.queue.QueueManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
fun editSaveButtonOnClick(
    context: Context,
    libraryViewModel: LibraryViewModel,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowPopupSong: (Boolean) -> Unit,
    song: Song,
    imageUri: Uri?,
    title: String,
    artist: String,
    queueManager: QueueManager,
    currentSong: MutableState<Song?>
){
        val savedImageUri = if(imageUri!=null) copyUriToStorage(context,imageUri) else null
        val songUpdate = song.copy(
            image = savedImageUri.toString(),
            title = title,
            artist = artist
        )
        libraryViewModel.update(songUpdate)
        queueManager.updateSong(songUpdate)
        if(currentSong.value?.id == songUpdate.id) currentSong.value = songUpdate
    coroutineScope.launch {
        sheetState.hide()
        setShowPopupSong(false)
    }
}