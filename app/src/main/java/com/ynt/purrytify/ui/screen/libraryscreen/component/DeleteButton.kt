package com.ynt.purrytify.ui.screen.libraryscreen.component

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.utils.libraryscreenutils.deleteFileFromExternalStorage
import com.ynt.purrytify.utils.libraryscreenutils.deleteFileFromStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteButton(
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    setShowPopupSong: (Boolean) -> Unit,
    song: Song,
    libraryViewModel: LibraryViewModel,
){
    val context = LocalContext.current
    Button(
        colors = ButtonDefaults.buttonColors(Color.Red),
        onClick = {
            deleteFileFromExternalStorage(context, song.audio?.toUri() ?: "".toUri())
            deleteFileFromStorage(song.image?.toUri() ?: "".toUri())
            libraryViewModel.delete(song)
            coroutineScope.launch {
                sheetState.hide()
                setShowPopupSong(false)
            }
        },
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth(1/2f)
            .padding(horizontal = 10.dp)){
        Text(
            text = "Delete",
            color = Color.White
        )
    }

}
