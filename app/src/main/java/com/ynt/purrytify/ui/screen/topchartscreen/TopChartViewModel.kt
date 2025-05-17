package com.ynt.purrytify.ui.screen.topchartscreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response

class TopChartViewModel(application: Application) : AndroidViewModel(application) {
    private val _onlineSongs = MutableLiveData<List<OnlineSong>>()
    val onlineSongs: LiveData<List<OnlineSong>> = _onlineSongs
    private val mSongRepository: SongRepository = SongRepository(application)

    fun loadTopSongs(isRegion : Boolean, sessionManager : SessionManager) {
        viewModelScope.launch {
            try {
                val profileResponse = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                val profile = profileResponse.body()
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
                Log.d("Error", e.message.toString())
            }
        }
    }

    fun insert(song: Song){
        viewModelScope.launch() {
            mSongRepository.insert(song)
        }

    }
}