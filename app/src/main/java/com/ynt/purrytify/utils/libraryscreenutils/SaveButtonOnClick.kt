package com.ynt.purrytify.ui.screen.libraryscreen.utils

import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
fun saveButtonOnClick(
    songUri: Uri?,
    imageUri: Uri?,
    duration: Int,
    context: Context,
    libraryViewModel: LibraryViewModel,
    title:String,
    artist:String,
    songOwner:String,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowPopupSong: (Boolean) -> Unit,
){
    if (songUri != null) {
        val savedSongUri = copyUriToExternalStorage(context,songUri,
            getFileNameFromUri(context, songUri)
        )
        val savedImageUri = if(imageUri!=null) copyUriToStorage(context,imageUri) else null
        val song = Song(
            title = title,
            artist = artist,
            owner = songOwner,
            image = savedImageUri.toString(),
            audio = savedSongUri.toString(),
            duration = duration
        )
        libraryViewModel.insert(song)
    }
    coroutineScope.launch {
        sheetState.hide()
        setShowPopupSong(false)
    }
}