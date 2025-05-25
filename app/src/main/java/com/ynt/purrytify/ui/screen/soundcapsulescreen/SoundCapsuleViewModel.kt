package com.ynt.purrytify.ui.screen.soundcapsulescreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ynt.purrytify.database.SongRepository


class SoundCapsuleViewModel(application: Application) : AndroidViewModel(application) {
    private val songRepo = SongRepository(application = application)

    fun loadTimeListened() {

    }

    fun loadTopArtist() {

    }

    fun loadTopSong() {

    }
}