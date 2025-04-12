package com.ynt.purrytify.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.data.model.ProfileResponse
import com.ynt.purrytify.data.network.RetrofitInstance
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.repository.SongRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val songRepo = SongRepository(application)
    val songList = MutableLiveData<List<Song>>()
    private val api = RetrofitInstance.api
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data
//    private val _text = MutableLiveData<String>().apply {
//        value = "Home"
//    }
//    val text: LiveData<String> = _text

    fun loadNewSongs(token: String?) {
        viewModelScope.launch {
            try {
                val response = api.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    songRepo.getNewSongs(response.body()?.username ?: "-").observeForever { songs ->
                        songList.postValue(songs)
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