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
){

        val savedImageUri = if(imageUri!=null) copyUriToStorage(context,imageUri) else null
        libraryViewModel.update(
            song.copy(
                image = savedImageUri.toString(),
                title = title,
                artist = artist
            )
        )
    coroutineScope.launch {
        sheetState.hide()
        setShowPopupSong(false)
    }
}