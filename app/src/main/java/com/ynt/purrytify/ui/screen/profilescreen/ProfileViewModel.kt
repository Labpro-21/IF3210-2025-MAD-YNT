package com.ynt.purrytify.ui.screen.profilescreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.MaxStreak
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.TenTopSong
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TimeListenedPerDay
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.models.TopTenArtist
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val monthLive = MutableLiveData<String>()
    val yearLive = MutableLiveData<Int>()
    val countSong = MutableLiveData<Int>()
    val countLiked = MutableLiveData<Int>()
    val playedCount = MutableLiveData<Int>()
    val timeListened = MutableLiveData<List<TimeListened>>()
    var monthlyTimeListened: LiveData<List<TimeListened>> = MutableLiveData()
    private val timeListenedObserver = Observer<List<TimeListened>> { time ->
        timeListened.postValue(time)
    }
    val songPerMonth = MutableLiveData<Int>()
    val artistPerMonth = MutableLiveData<Int>()
    val timeListenedPerMonth = MutableLiveData<Long>()

    val topSongs = MutableLiveData<List<TopSong>>()
    val topArtists = MutableLiveData<List<TopArtist>>()
    var listTopSong = MutableLiveData<List<Song>>()
    val listTopArtist = MutableLiveData<List<Song>>()
    val longestStreakSong = MutableLiveData<List<MaxStreak>>()
    val listTopTenSong = MutableLiveData<List<TenTopSong>>()
    val listTopTenArtist = MutableLiveData<List<TopTenArtist>>()
    val listTimeListened = MutableLiveData<List<TimeListenedPerDay>>()
    private val songRepo = SongRepository(application = application)
    private val _data = MutableLiveData<Result<ProfileResponse?>>()
    val data: LiveData<Result<ProfileResponse?>> = _data

    fun loadProfile(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProfile("Bearer ${sessionManager.getAccessToken()}")
                if (response.isSuccessful) {
                    _data.value = Result.success(response.body())

                    val countSongPerUser = songRepo.countSongsPerUser(response.body()?.email ?: "-")
                    countSong.postValue(countSongPerUser)

                    val countLikedPerUser = songRepo.countLikedSong(username = response.body()?.email ?: "-")
                    countLiked.postValue(countLikedPerUser)

                    val countPlayedPerUser = songRepo.playedSongCount(username = response.body()?.email ?: "-")
                    playedCount.postValue(countPlayedPerUser)

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
        Log.d("ProfileViewModel", "User fetched: $user")

        viewModelScope.launch {
            val count = songRepo.songStatCountForUser(user)
            Log.d("ProfileViewModel", "The Count is $count")

            if (count > 0) {
                monthlyTimeListened.removeObserver(timeListenedObserver)
                monthlyTimeListened = songRepo.getMonthlyTimeListened(user)
                monthlyTimeListened.observeForever(timeListenedObserver)

                val monthlySongCount = songRepo.getMonthlySongCount(user)
                Log.d("ProfileViewModel", "Monthly song count: $monthlySongCount")
                topSongs.postValue(monthlySongCount)

                val songIds = monthlySongCount.map { it.songId.toInt() }
                val songMap = songRepo.getOneSongById(user, songIds.distinct())
                    .associateBy { it.id }
                val oneSongById = songIds.mapNotNull { songMap[it] }
                Log.d("ProfileViewModel", "One song by ID: $oneSongById")
                listTopSong.postValue(oneSongById)

                val monthlyArtistCount = songRepo.getMonthlyArtistCount(user)
                Log.d("ProfileViewModel", "Monthly artist count: $monthlyArtistCount")
                topArtists.postValue(monthlyArtistCount)

                val artists = monthlyArtistCount.map { it.artists }
                Log.d("ProfileViewModel", "Top artist names (raw): $artists")
                val uniqueArtists = artists.distinct()
                val artistToSongMap = songRepo.getOneSongByArtist(user, uniqueArtists)
                    .associateBy { it.artist }
                val oneSongByArtist = artists.mapNotNull { artistToSongMap[it] }
                Log.d("ProfileViewModel", "One song by artist (with possible duplicates): $oneSongByArtist")
                listTopArtist.postValue(oneSongByArtist.reversed())

                val longestActiveStreakSong = songRepo.getMonthlyMaxStreaksForUser(user)
                Log.d("ProfileViewModel", "Longest active streak songs: $longestActiveStreakSong")
                longestStreakSong.postValue(longestActiveStreakSong.reversed())
            } else {
                Log.d("ProfileViewModel", "No song stats available for user. Posting empty lists.")

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

    fun getTopSong(sessionManager: SessionManager, month: Int, year: Int) {
        val user = sessionManager.getUser()
        viewModelScope.launch {
            monthLive.postValue(Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH))
            yearLive.postValue(year)
            val listMonth = topSongs.value?.map { it.month }
            val listYear = topSongs.value?.map { it.year }
            if (listMonth != null && listYear != null) {
                val tenTopSong = songRepo.getTenTopSong(user, month, year)
                listTopTenSong.postValue(tenTopSong)

                val monthSong = songRepo.getAllSongPerMonth(user, month, year)
                songPerMonth.postValue(monthSong)
            }
        }
    }

    fun getTopArtist(sessionManager: SessionManager, month: Int, year: Int) {
        val user = sessionManager.getUser()
        viewModelScope.launch {
            monthLive.postValue(Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH))
            yearLive.postValue(year)
            val listMonth = topSongs.value?.map { it.month }
            val listYear = topSongs.value?.map { it.year }
            if (listMonth != null && listYear != null) {
                val tenTopArtist = songRepo.getTenTopArtist(user, month, year)
                listTopTenArtist.postValue(tenTopArtist)

                val monthArtist = songRepo.getAllArtistPerMonth(user, month, year)
                artistPerMonth.postValue(monthArtist)
            }
        }
    }

    fun getMonthlyTimeListened(sessionManager: SessionManager, month: Int, year: Int) {
        val user = sessionManager.getUser()
        viewModelScope.launch {
            monthLive.postValue(Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH))
            yearLive.postValue(year)
            val listMonth = topSongs.value?.map { it.month }
            val listYear = topSongs.value?.map { it.year }
            if (listMonth != null && listYear != null) {
                val listTime = songRepo.getTimeListenedPerDay(user, month, year)
                listTimeListened.postValue(listTime)

                val time = songRepo.getTotalTimeListenedInMonth(user, month, year)
                timeListenedPerMonth.postValue(time)
            }
        }
    }
}