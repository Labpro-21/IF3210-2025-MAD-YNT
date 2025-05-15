package com.ynt.purrytify.utils.libraryscreenutils

import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.libraryscreen.utils.copyUriToStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
    currentSong: MutableStateFlow<Song>?
){
        val savedImageUri = if(imageUri!=null) copyUriToStorage(context,imageUri) else null
        val songUpdate = song.copy(
            image = savedImageUri.toString(),
            title = title,
            artist = artist
        )
        libraryViewModel.update(songUpdate)
        currentSong?.update { songUpdate }

    coroutineScope.launch {
        sheetState.hide()
        setShowPopupSong(false)
    }
}