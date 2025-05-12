package com.ynt.purrytify.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.SessionManager
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val songRepo = SongRepository(application)
    val songList = MutableLiveData<List<Song>>()
    val songListRecently = MutableLiveData<List<Song>>()
    private val api = RetrofitInstance.api
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadNewSongs(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                val response = api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                if (response.isSuccessful) {
                    songRepo.getNewSongs(response.body()?.username ?: "-").observeForever { songs ->
                        songList.postValue(songs)
                    }
                    songRepo.getRecentlyPLayed(response.body()?.username ?: "-").observeForever { songs ->
                        songListRecently.postValue(songs)
                    }
                } else {
                    _data.value = Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                _data.value = Result.failure(e)
            }

        }
    }
}