package com.ynt.purrytify.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val songRepo = SongRepository(application)
    val songList = MutableLiveData<List<Song>>()
    val songListRecently = MutableLiveData<List<Song>>()
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadNewSongs(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                if (response.isSuccessful) {
                    songRepo.getNewSongs(response.body()?.email ?: "-").observeForever { songs ->
                        songList.postValue(songs)
                    }
                    songRepo.getRecentlyPLayed(response.body()?.email ?: "-").observeForever { songs ->
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