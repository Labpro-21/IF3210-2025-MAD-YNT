package com.ynt.purrytify.ui.library.utils

import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.ui.library.LibraryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
fun saveButtonOnClick(
    songUri: Uri?,
    imageUri: Uri?,
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
}