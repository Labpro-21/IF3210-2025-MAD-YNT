package com.ynt.purrytify.ui.library

import androidx.recyclerview.widget.DiffUtil
import com.ynt.purrytify.database.song.Song

class SongDiffCallback(private val oldSongList: List<Song>, private val newSongList: List<Song>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldSongList.size
    override fun getNewListSize(): Int = newSongList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldSongList[oldItemPosition].id == newSongList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSong = oldSongList[oldItemPosition]
        val newSong = newSongList[newItemPosition]
        return oldSong.title == newSong.title && oldSong.artist == newSong.artist
    }
}