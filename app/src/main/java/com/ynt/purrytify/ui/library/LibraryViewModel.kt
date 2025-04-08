package com.ynt.purrytify.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.repository.SongRepository


class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val mSongRepository: SongRepository = SongRepository(application)
    fun getAllSongs(): LiveData<List<Song>> = mSongRepository.getAllSongs()

    fun insert(song: Song){
        mSongRepository.insert(song)
    }

    fun update(song: Song){
        mSongRepository.update(song)
    }

    fun delete(song: Song){
        mSongRepository.delete(song)
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is library Fragment"
    }
    val text: LiveData<String> = _text
}