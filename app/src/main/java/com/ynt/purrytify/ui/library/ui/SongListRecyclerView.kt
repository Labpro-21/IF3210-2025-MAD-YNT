package com.ynt.purrytify.ui.library.ui

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
import com.ynt.purrytify.ui.library.LibraryViewModel
import com.ynt.purrytify.ui.library.ListSongAdapter

@Composable
fun SongListRecyclerView(
    localContext: Context,
    viewModel: LibraryViewModel,
    lifecycleOwner: LifecycleOwner,
    loggedInUser: String
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
            val listSongAdapter = ListSongAdapter(emptyList())
            rvSongs.adapter = listSongAdapter
            viewModel.getAllSongs(loggedInUser).observe(lifecycleOwner) { songList ->
                if (songList != null) {
                    listSongAdapter.setListSongs(songList)
                }
            }
            view
        },
    )
}