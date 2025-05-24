package com.ynt.purrytify.ui.screen.libraryscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.Song


class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val mSongRepository: SongRepository = SongRepository(application)

    fun getAllSongs(username: String): LiveData<List<Song>> = mSongRepository.getAllSongs(username)

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