package com.ynt.purrytify.ui.screen.libraryscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ynt.purrytify.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: LibraryViewModel = viewModel()
){
    val showPopUpAddSong = remember { mutableStateOf(false) }
    val showPopUpEditSong = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    val selectedChoiceIndex = remember { mutableIntStateOf(0) }
    val editedSong = remember { mutableStateOf<Song?>(null) }
    val showMediaPlayer = remember { mutableStateOf(false) }
    val playedSong = remember { mutableStateOf<Song?>(null) }
    val username = sessionManager.getUser()

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
                onChoiceSelected = { selectedChoiceIndex.intValue = it }
            )

        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
                SongListRecyclerView(
                    localContext = localContext,
                    lifecycleOwner = lifecycleOwner,
                    loggedInUser = username,
                    viewModel = viewModel,
                    choice = selectedChoiceIndex.intValue,
                    updateLikeSong = {
                        val songCopy = it.copy(isLiked = if (it.isLiked == 1) 0 else 1)
                        viewModel.update(songCopy)
                    },
                    updateEditSong = {
                        editedSong.value = it
                        showPopUpEditSong.value = true
                    },
                    playSong = {
                        showMediaPlayer.value = true
                        playedSong.value = it
                    }
                )
                if (showPopUpEditSong.value && editedSong.value != null) {
                    EditSong(
                        setShowPopupSong = { showPopUpEditSong.value= it },
                        libraryViewModel = viewModel,
                        loggedInUser = username,
                        context = localContext,
                        sheetState = sheetState,
                        song = editedSong.value!!,
                    )
                }
        }
        }
}