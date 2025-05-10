package com.ynt.purrytify.ui.screen.libraryscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.network.RetrofitInstance
import kotlinx.coroutines.launch


class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val mSongRepository: SongRepository = SongRepository(application)

    private val _profile = MutableLiveData<ProfileResponse?>()
    val profile: LiveData<ProfileResponse?> = _profile

    private val _loggedInUser = MutableLiveData<String>().apply {
        value = ""
    }
    val loggedInUser: LiveData<String> = _loggedInUser

    fun getAllSongs(username: String): LiveData<List<Song>> = mSongRepository.getAllSongs(username)

    fun insert(song: Song){
        mSongRepository.insert(song)
    }

    fun update(song: Song){
        mSongRepository.update(song)
    }

    fun delete(song: Song){
        mSongRepository.delete(song)
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is library Fragment"
    }
    val text: LiveData<String> = _text

    fun loadUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                val response = RetrofitInstance.api.getProfile(authHeader)
                if (response.isSuccessful) {
                    _profile.postValue(response.body())
                    _loggedInUser.postValue(response.body()?.username)
                } else {
                    _profile.postValue(null)
                    _loggedInUser.postValue("Error fetching user data")
                }
            } catch (e: Exception) {
                _profile.postValue(null)
                _loggedInUser.postValue("Error fetching user data")
            }
        }
    }

}