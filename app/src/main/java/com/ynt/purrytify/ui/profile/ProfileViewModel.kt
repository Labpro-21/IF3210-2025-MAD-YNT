package com.ynt.purrytify.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.data.model.ProfileResponse
import com.ynt.purrytify.data.network.RetrofitInstance
import com.ynt.purrytify.repository.SongRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val songRepo = SongRepository(application)
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    //    private val _text = MutableLiveData<String>().apply {
//        value = "This is profile Fragment"
//    }
//    val text: LiveData<String> = _text
    private val api = RetrofitInstance.api
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadProfile(token: String?) {
        viewModelScope.launch {
            try {
                val response = api.getProfile("Bearer $token")
//                Log.d("ProfileViewModel", "Response status: ${response.code()}, success: ${response.isSuccessful}")
                if (response.isSuccessful) {
                    val data = response.body()
                    _data.value = Result.success(data)
                    songRepo.countSongsPerUser(response.body()?.username ?: "-").observeForever { count ->
                        countSong.postValue(count)
                    }
                    songRepo.countLikedSong(username = response.body()?.username ?: "-").observeForever { count ->
                        countLiked.postValue(count)
                    }
                } else {
//                    Log.d("ProfileViewModel", "ayam else")
                    _data.value = Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
//                Log.e("ProfileViewModel", "Error saat memuat profile: ${e.message}", e)
//                Log.d("ProfileViewModel", "ayam catch")
                _data.value = Result.failure(e)
            }

        }
    }
}