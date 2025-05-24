package com.ynt.purrytify.ui.screen.profilescreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collection.MutableVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    val playedCount = MutableLiveData<Int>()
    val timeListened = MutableLiveData<List<TimeListened>>()
    val topSongs = MutableLiveData<List<TopSong>>()
    val topArtists = MutableLiveData<List<TopArtist>>()
    private val songRepo = SongRepository(application = application)
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadProfile(sessionManager: SessionManager) {
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

    fun getSoundCapsuleData(sessionManager: SessionManager) {
        val user = sessionManager.getUser()
        songRepo.getMonthlyTimeListened(user).observeForever { result ->
            timeListened.postValue(result)
        }
        songRepo.getMonthlySongCount(user).observeForever { result ->
            topSongs.postValue(result)
        }
        songRepo.getMonthlyArtistCount(user).observeForever { result ->
            topArtists.postValue(result)
        }
    }
}