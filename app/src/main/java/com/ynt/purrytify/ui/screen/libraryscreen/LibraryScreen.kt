package com.ynt.purrytify.ui.screen.libraryscreen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.component.AddSong
import com.ynt.purrytify.ui.screen.libraryscreen.component.EditSong
import com.ynt.purrytify.ui.screen.libraryscreen.component.LibraryTopBar
import com.ynt.purrytify.ui.screen.libraryscreen.component.SongListRecyclerView
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: LibraryViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel,
    showSongPlayerSheet: MutableState<Boolean>,
    configuration: Configuration
) {
    val showPopUpAddSong = remember { mutableStateOf(false) }
    val showPopUpEditSong = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    val selectedChoiceIndex = rememberSaveable { mutableIntStateOf(0) }
    val editedSong = remember { mutableStateOf<Song?>(null) }
    val username = sessionManager.getUser()
    val songList by viewModel.getAllSongs(username).observeAsState()

    var isTopBarVisible = remember { mutableStateOf(true) }

    LaunchedEffect(songList) {
        val list = songList
        if(list!=null) {
            playbackViewModel.syncLocal(list)
        }
        if (playbackViewModel.sourceName=="local"){
            playbackViewModel.setLocal()
        }
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
            if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                AnimatedContent(targetState = isTopBarVisible.value) {
                    if(it){
                    LibraryTopBar(
                            title = "Your Library",
                            onAddClick = { showPopUpAddSong.value = true },
                            selectedChoiceIndex = selectedChoiceIndex.intValue,
                            onChoiceSelected = { selectedChoiceIndex.intValue = it },
                            navController = navController
                        )
                    }
                }
            }
            else{
                LibraryTopBar(
                    title = "Your Library",
                    onAddClick = { showPopUpAddSong.value = true },
                    selectedChoiceIndex = selectedChoiceIndex.intValue,
                    onChoiceSelected = { selectedChoiceIndex.intValue = it },
                    navController = navController
                )
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SongListRecyclerView(
                isTopBarVisible = isTopBarVisible,
                localContext = localContext,
                lifecycleOwner = lifecycleOwner,
                loggedInUser = username,
                viewModel = viewModel,
                choice = selectedChoiceIndex.intValue,
                updateLikeSong = { song ->
                    val songCopy = song.copy(isLiked = if (song.isLiked == 1) 0 else 1)
                    viewModel.update(songCopy)
                    if(songCopy.id == playbackViewModel.currentMediaId){
                        playbackViewModel.currentSong = songCopy
                    }
                },
                updateEditSong = { song ->
                    showPopUpEditSong.value = true
                    editedSong.value = song
                },
                playSong = { selectedSong ->
                    if(playbackViewModel.currentMediaId == selectedSong.id && playbackViewModel.isPlaying){
                        showSongPlayerSheet.value = true
                    }
                    else{
                        val songCopy = selectedSong.copy(lastPlayed = System.currentTimeMillis())
                        viewModel.update(songCopy)
                        playbackViewModel.setLocal()
                        playbackViewModel.playSongById(songCopy.id.toString())
                    }
                }
            )

            if (showPopUpEditSong.value && editedSong.value != null) {
                EditSong(
                    setShowPopupSong = { showPopUpEditSong.value = it },
                    libraryViewModel = viewModel,
                    playbackViewModel = playbackViewModel,
                    loggedInUser = username,
                    context = localContext,
                    sheetState = sheetState,
                    song = editedSong.value!!,
                )
            }
        }
    }
}