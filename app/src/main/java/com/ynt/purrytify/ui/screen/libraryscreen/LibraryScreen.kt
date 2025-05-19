package com.ynt.purrytify.ui.screen.libraryscreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.component.AddSong
import com.ynt.purrytify.ui.screen.libraryscreen.component.EditSong
import com.ynt.purrytify.ui.screen.libraryscreen.component.LibraryTopBar
import com.ynt.purrytify.ui.screen.libraryscreen.component.SongListRecyclerView
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    sessionManager: SessionManager,
    currentSong: MutableStateFlow<Song>?,
    viewModel: LibraryViewModel = viewModel(),
    showSongPlayerSheet: MutableState<Boolean>,
    onPlay: (song: Song)->Unit,
    onSongsLoaded: (List<Song>?) -> Unit = {}
) {
    val showPopUpAddSong = remember { mutableStateOf(false) }
    val showPopUpEditSong = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    val selectedChoiceIndex = rememberSaveable { mutableIntStateOf(0) }
    val editedSong = remember { mutableStateOf<Song?>(null) }

    val username = sessionManager.getUser()

    val songList by viewModel.getAllSongs(username ?: "").observeAsState(emptyList())

    LaunchedEffect(songList) {
        onSongsLoaded(songList)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showPopUpAddSong.value) {
        AddSong(
            setShowPopupSong = { showPopUpAddSong.value = it },
            libraryViewModel = viewModel,
            loggedInUser = username,
            context = localContext,
            sheetState = sheetState,
        )
    }

    Scaffold(
        topBar = {
            LibraryTopBar(
                title = "Your Library",
                onAddClick = { showPopUpAddSong.value = true },
                selectedChoiceIndex = selectedChoiceIndex.intValue,
                onChoiceSelected = { selectedChoiceIndex.intValue = it },
                navController = navController
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SongListRecyclerView(
                localContext = localContext,
                lifecycleOwner = lifecycleOwner,
                loggedInUser = username,
                viewModel = viewModel,
                choice = selectedChoiceIndex.intValue,
                updateLikeSong = { song ->
                    val songCopy = song.copy(isLiked = if (song.isLiked == 1) 0 else 1)
                    viewModel.update(songCopy)
                    if(songCopy.id == currentSong?.value?.id ?: null){
                        currentSong?.update {
                            songCopy
                        }
                    }

                },
                updateEditSong = { song ->
                    editedSong.value = song
                    showPopUpEditSong.value = true
                },
                playSong = { selectedSong ->
                    if(currentSong?.value?.id ?: null == selectedSong.id){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        onPlay(selectedSong)
                    }
                }
            )

            if (showPopUpEditSong.value && editedSong.value != null) {
                EditSong(
                    setShowPopupSong = { showPopUpEditSong.value = it },
                    libraryViewModel = viewModel,
                    loggedInUser = username,
                    context = localContext,
                    sheetState = sheetState,
                    song = editedSong.value!!,
                    currentSong = currentSong
                )
            }
        }
    }
}