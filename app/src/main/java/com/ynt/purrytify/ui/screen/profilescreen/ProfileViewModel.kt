package com.ynt.purrytify.ui.screen.profilescreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.concurrent.timer

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    val playedCount = MutableLiveData<Int>()
    val timeListened = MutableLiveData<List<TimeListened>>()
    val topSongs = MutableLiveData<List<TopSong>>()
    val topArtists = MutableLiveData<List<TopArtist>>()
    var listTopSong = MutableLiveData<List<Song>>()
    val listTopArtist = MutableLiveData<List<Song>>()
    val listTopTenSong = MutableLiveData<List<Song>>()
    val listTopTenArtist = MutableLiveData<List<Song>>()
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
            songRepo.getOneSongById(user, result.map { it.songId.toInt() }).observeForever { res ->
                listTopSong.postValue(res)
            }
            topSongs.postValue(result)
        }
        songRepo.getMonthlyArtistCount(user).observeForever { result ->
            songRepo.getOneSongByArtist(user, result.map { it.artists }).observeForever { res ->
                listTopArtist.postValue(res)
            }
            topArtists.postValue(result)
        }
    }

    fun getCsv() : List<List<String>> {
        val data = mutableListOf<List<String>>()
        data.add(listOf("Sound Capsule Report"))
        data.add(listOf("Created at ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}"))
        data.add(listOf(""))

        val time = timeListened.value
        var listSong = listTopSong.value
        val listArtist = listTopArtist.value

        time?.let { list ->
            for (i in list.indices) {
                data.add(listOf("month", Month.of(time[i].month).name.lowercase().replaceFirstChar { it.uppercase() }))
                data.add(listOf("year", "${time[i].year}"))
                data.add(listOf("Time Listened", "${time[i].timeListened/60000} minutes"))
                data.add(listOf("Average Daily Listening Time", "0"))
                data.add(listOf("Top Listened Song", "${listSong?.get(i)?.title}"))
                data.add(listOf("Top Listened Artist", "${listArtist?.get(i)?.artist}"))
                data.add(listOf(""))
                data.add(listOf("Top 10 Songs"))
                for (j in 0 until 10) {
                    data.add(listOf("song"))
                }
                data.add(listOf("Top 10 Artists"))
                for (j in 0 until 10) {
                    data.add(listOf("artist"))
                }
            }
        }
        return data
    }

    fun getTopSong(sessionManager: SessionManager) {
        val user = sessionManager.getUser()
        val listMonth = topSongs.value?.map { it.month }
        val listYear = topSongs.value?.map { it.year }
        if (listMonth != null && listYear != null) {
            songRepo.getTenTopSong(user, listMonth, listYear).observeForever { result ->
                listTopTenSong.postValue(result)
            }
        }
    }
}