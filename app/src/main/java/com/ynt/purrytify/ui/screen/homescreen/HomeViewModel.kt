package com.ynt.purrytify.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val songRepo = SongRepository(application)
    val songList = MutableLiveData<List<Song>>()
    val songListRecently = MutableLiveData<List<Song>>()
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadHome(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                if (response.isSuccessful) {
                    Log.d("Successful", "success")
                    _data.value = Result.success(response.body())
                    response.body()?.let { sessionManager.setProfile(it) }
                    songRepo.getNewSongs(sessionManager.getProfile()["email"] ?: "-").observeForever { songs ->
                        songList.postValue(songs)
                    }
                    songRepo.getRecentlyPLayed(sessionManager.getProfile()["email"] ?: "-").observeForever { songs ->
                        songListRecently.postValue(songs)
                    }
                } else {
                    _data.value = Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch(e: IOException) {
                Log.e("IOException", "No internet")
                songRepo.getNewSongs(sessionManager.getUser()).observeForever { songs ->
                    songList.postValue(songs)
                }
                songRepo.getRecentlyPLayed(sessionManager.getUser()).observeForever { songs ->
                    songListRecently.postValue(songs)
                }
            } catch (e: Exception) {
                _data.value = Result.failure(e)
            }
        }
    }

    fun update(song: Song) {
        songRepo.update(song)
    }
}