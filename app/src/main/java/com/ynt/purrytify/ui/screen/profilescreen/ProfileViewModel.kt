package com.ynt.purrytify.ui.screen.profilescreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    val playedCount = MutableLiveData<Int>()
    private val songRepo = SongRepository(application = application)
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    suspend fun loadProfile(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                if (response.isSuccessful) {
                    _data.value = Result.success(response.body())

                    songRepo.countSongsPerUser(response.body()?.email ?: "-").observeForever { count ->
                        countSong.postValue(count)
                    }
                    songRepo.countLikedSong(username = response.body()?.email ?: "-").observeForever { count ->
                        countLiked.postValue(count)
                    }
                    songRepo.playedSongCount(username = response.body()?.email ?: "-").observeForever { count ->
                        playedCount.postValue(count)
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