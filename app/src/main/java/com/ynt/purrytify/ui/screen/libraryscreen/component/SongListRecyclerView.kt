package com.ynt.purrytify.ui.screen.libraryscreen.component

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.libraryscreen.adapter.ListSongAdapter

@Composable
fun SongListRecyclerView(
    localContext: Context,
    viewModel: LibraryViewModel,
    lifecycleOwner: LifecycleOwner,
    loggedInUser: String,
    choice: Int,
    updateLikeSong: (Song) -> Unit,
    updateEditSong: (Song) -> Unit,
    playSong:   (Song) -> Unit
){
    AndroidView(
        modifier = Modifier,
        factory = {
            val view = View.inflate(it, R.layout.recyclerview_library, null).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val rvSongs: RecyclerView = view.findViewById(R.id.rv_songs)
            rvSongs.setHasFixedSize(true)
            rvSongs.layoutManager = LinearLayoutManager(localContext)
            val listSongAdapter = ListSongAdapter(emptyList(),{},{},{})
            rvSongs.adapter = listSongAdapter
            viewModel.getAllSongs(loggedInUser).observe(lifecycleOwner) { songList ->
                if (songList != null) {
                    listSongAdapter.setListSongs(songList)
                }
            }
            view
        },
        update = { view ->
            val rvSongs: RecyclerView = view.findViewById(R.id.rv_songs)
            rvSongs.setHasFixedSize(true)
            rvSongs.layoutManager = LinearLayoutManager(localContext)
            val listSongAdapter = ListSongAdapter(
                listSong = emptyList(),
                likeSong = {song: Song -> updateLikeSong(song) },
                editSong = {song: Song -> updateEditSong(song)},
                playSong = {song: Song -> playSong(song)}
            )
            rvSongs.adapter = listSongAdapter
            viewModel.getAllSongs(loggedInUser).observe(lifecycleOwner) { songList ->
                val songFiltered = when(choice){
                    0 -> songList
                    1 -> songList.filter { it.isLiked == 1 }
                    else -> songList
                }
                if (songList != null) {
                    listSongAdapter.setListSongs(songFiltered)
                }
//                rvSongs.scrollToPosition(0)
            }
        }
    )
}
