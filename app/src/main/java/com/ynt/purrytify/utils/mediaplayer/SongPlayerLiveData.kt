package com.ynt.purrytify.utils.mediaplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SongPlayerLiveData(application: Application): AndroidViewModel(application) {
    private val _songPlayer = MutableLiveData<SongPlayer>().apply {
        value = SongPlayer()
    }
    val songPlayer: LiveData<SongPlayer> = _songPlayer
}