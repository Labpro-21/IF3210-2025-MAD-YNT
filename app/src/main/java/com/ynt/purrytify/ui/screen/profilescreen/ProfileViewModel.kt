package com.ynt.purrytify.ui.screen.profilescreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.MaxStreak
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

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    val playedCount = MutableLiveData<Int>()
    val timeListened = MutableLiveData<List<TimeListened>>()
    val topSongs = MutableLiveData<List<TopSong>>()
    val topArtists = MutableLiveData<List<TopArtist>>()
    var listTopSong = MutableLiveData<List<Song>>()
    val listTopArtist = MutableLiveData<List<Song>>()
    val longestStreakSong = MutableLiveData<List<MaxStreak>>()
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
        viewModelScope.launch {
            val count = songRepo.songStatCountForUser(user)
            Log.d("ProfileViewModel", "The Count is $count")
            if (count > 0) {
                val monthlyTimeListened = songRepo.getMonthlyTimeListened(user)
                timeListened.postValue(monthlyTimeListened)
                val monthlySongCount = songRepo.getMonthlySongCount(user)
                topSongs.postValue(monthlySongCount)
                val songIds = monthlySongCount.map { it.songId.toInt() }
                val oneSongById = songRepo.getOneSongById(user, songIds)
                listTopSong.postValue(oneSongById)
                val monthlyArtistCount = songRepo.getMonthlyArtistCount(user)
                topArtists.postValue(monthlyArtistCount)
                val artists = monthlyArtistCount.map { it.artists }
                val oneSongByArtist = songRepo.getOneSongByArtist(user, artists)
                listTopArtist.postValue(oneSongByArtist)
                val longestActiveStreakSong = songRepo.getMonthlyMaxStreaksForUser(user)
                longestStreakSong.postValue(longestActiveStreakSong)
            } else {
                timeListened.postValue(emptyList())
                listTopSong.postValue(emptyList())
                topSongs.postValue(emptyList())
                listTopArtist.postValue(emptyList())
                topArtists.postValue(emptyList())
                longestStreakSong.postValue(emptyList())
            }
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
}