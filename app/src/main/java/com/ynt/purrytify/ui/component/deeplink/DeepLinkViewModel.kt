package com.ynt.purrytify.ui.component.deeplink

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.downloadmanager.parseDurationToSeconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeepLinkViewModel(application: Application) : AndroidViewModel(application) {
    private val _onlineSong = MutableStateFlow<OnlineSong?>(null)
    val onlineSong: StateFlow<OnlineSong?> = _onlineSong.asStateFlow()
    private var profile: ProfileResponse? = null

    fun loadSong(songId: String) {
        viewModelScope.launch {
            try {
                Log.d("DeepLink", "Fetching song $songId")
                val response = RetrofitInstance.api.getSongById(songId)
                if (response.isSuccessful) {
                    Log.d("DeepLink", "Fetched successfully: ${response.body()}")
                    _onlineSong.value = response.body()
                } else {
                    Log.e("DeepLink", "Response failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DeepLink", "Exception: ${e.message}")
            }
        }
    }

    fun convertOnlineSongToSong(onlineSong: OnlineSong?): Song? {
        if(onlineSong == null) return null
        return Song(
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
