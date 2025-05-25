package com.ynt.purrytify.ui.screen.topchartscreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.downloadmanager.parseDurationToSeconds
import kotlinx.coroutines.launch
import retrofit2.Response

class TopChartViewModel(application: Application) : AndroidViewModel(application) {
    private val _onlineSongs = MutableLiveData<List<OnlineSong>>()
    val onlineSongs: LiveData<List<OnlineSong>> = _onlineSongs
    val currentRegion = MutableLiveData<String>()
    private val mSongRepository: SongRepository = SongRepository(application)
    private var profile: ProfileResponse? = null

    fun loadTopSongs(isRegion : Boolean, sessionManager : SessionManager) {
        viewModelScope.launch {
            try {
                val profileResponse = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                profile = profileResponse.body()
                currentRegion.value = profile?.location
                val response : Response<List<OnlineSong>>
                if (isRegion) {
                    response = RetrofitInstance.api.getTopRegionSongs(profile?.location ?: "")
                } else {
                    response = RetrofitInstance.api.getTopGlobalSongs()
                }

                if (response.isSuccessful) {
                    _onlineSongs.value = response.body()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun insert(song: Song){
        viewModelScope.launch {
            mSongRepository.insert(song)
        }
    }

    fun update(song: Song) {
        mSongRepository.update(song)
    }

    fun convertOnlineSongToSong(source: List<OnlineSong>): List<Song> {
        return source.map { onlineSong ->
            Song(
                id = onlineSong.id,
                title = onlineSong.title,
                artist = onlineSong.artist,
                owner = profile?.email,
                image = onlineSong.artwork,
                audio = onlineSong.url,
                duration = parseDurationToSeconds(onlineSong.duration)
            )
        }
    }

    fun getRegion(): String {
        return profile?.location ?: ""
    }
}